package it.polimi.travlendarplus.beans.calendarManager;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages.*;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.UserLocation;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.Constraint;
import it.polimi.travlendarplus.entities.preferences.DistanceConstraint;
import it.polimi.travlendarplus.entities.preferences.PeriodOfDayConstraint;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PreferenceManager extends UserManager {

    public List < TypeOfEvent > getPreferencesProfiles () {
        return currentUser.getPreferences();
    }

    public TypeOfEvent getPreferencesProfile ( long id ) throws EntityNotFoundException {
        List < TypeOfEvent > profiles = getPreferencesProfiles();
        TypeOfEvent requested = profiles.stream()
                .filter( typeOfEvent -> typeOfEvent.getId() == id )
                .findFirst().orElse( null );
        if ( requested == null ) {
            throw new EntityNotFoundException();
        }
        return requested;
    }

    public TypeOfEvent addTypeOfEvent ( AddTypeOfEventMessage typeOfEventMessage ) throws InvalidFieldException {
        checkTypeOfEventConsistency( typeOfEventMessage );
        TypeOfEvent typeOfEvent = createTypeOfEvent( typeOfEventMessage );
        typeOfEvent.save();
        currentUser.addPreference( typeOfEvent );
        currentUser.save();
        return typeOfEvent;
    }

    public TypeOfEvent modifyTypeOfEvent ( ModifyTypeOfEventMessage typeOfEventMessage )
            throws InvalidFieldException, EntityNotFoundException {
        //TODO call check on events that depends from it?
        checkTypeOfEventConsistency( typeOfEventMessage );
        TypeOfEvent typeOfEvent = createTypeOfEvent( typeOfEventMessage );
        typeOfEvent.setId( typeOfEventMessage.getId() );
        typeOfEvent.save();
        return typeOfEvent;
    }

    public void deleteTypeOfEvent ( long id ) throws EntityNotFoundException {
        //TODO what about event dependencies? remain relation only with events?
        TypeOfEvent typeOfEvent = getPreferencesProfile( id );
        currentUser.removePreference( id );
        currentUser.save();
        typeOfEvent.remove();
    }

    private void checkTypeOfEventConsistency ( AddTypeOfEventMessage typeOfEventMessage ) throws InvalidFieldException {
        List < String > errors = new ArrayList <>();
        if ( typeOfEventMessage.getName() == null ) {
            errors.add( "name" );
        }
        if ( typeOfEventMessage.getParamFirstPath() == null ) {
            errors.add( "paramFirstPath" );
        }

        errors.addAll( checkPeriodConstraints( typeOfEventMessage.getLimitedByPeriod() ) );
        errors.addAll( checkDistanceConstraints( typeOfEventMessage.getLimitedByDistance() ) );

        if ( errors.size() > 0 ) {
            throw new InvalidFieldException( errors );
        }
    }

    private List < String > checkPeriodConstraints ( List < AddPeriodConstraintMessage > periodConstraints ) {
        List < String > periodErrors = new ArrayList <>();
        //checks that min < max hour
        periodErrors.addAll( periodConstraints.stream()
                .filter( periodConstraint -> periodConstraint.getMinHour() > periodConstraint.getMaxHour() )
                .map( periodConstraint -> " periodConstraint " +
                        periodConstraints.indexOf( periodConstraint ) +
                        " min hour must be less than max hour" )
                .collect( Collectors.toList() ) );
        //checks that minHour >= 0
        periodErrors.addAll( periodConstraints.stream()
                .filter( periodConstraint -> periodConstraint.getMinHour() < 0 )
                .map( periodConstraint -> " periodConstraint " +
                        periodConstraints.indexOf( periodConstraint ) +
                        " min hour must be greater than zero" )
                .collect( Collectors.toList() ) );
        //checks that maxHour < 24 h
        periodErrors.addAll( periodConstraints.stream()
                .filter( periodConstraint -> periodConstraint.getMaxHour() >= 24 * 60 * 60 )
                .map( periodConstraint -> " periodConstraint " +
                        periodConstraints.indexOf( periodConstraint ) +
                        " max hour must be less than 24 h" )
                .collect( Collectors.toList() ) );
        periodErrors.addAll( checkTravelMeanEnum( new ArrayList <>( periodConstraints ) ) );

        return periodErrors;
    }

    private List < String > checkDistanceConstraints ( List < AddDistanceConstraintMessage > distanceConstraints ) {
        List < String > distanceErrors = new ArrayList <>();
        //checks that min length >= 0
        distanceErrors.addAll( distanceConstraints.stream()
                .filter( distanceConstraint -> distanceConstraint.getMinLength() < 0 )
                .map( distanceConstraint -> " distanceConstraint " +
                        distanceConstraints.indexOf( distanceConstraint ) +
                        " min length must be greater than zero" )
                .collect( Collectors.toList() ) );
        //checks that min length < max length
        distanceErrors.addAll( distanceConstraints.stream()
                .filter( distanceConstraint -> distanceConstraint.getMinLength() > distanceConstraint.getMaxLength() )
                .map( distanceConstraint -> " distanceConstraint " +
                        distanceConstraints.indexOf( distanceConstraint ) +
                        " min length must be less than max length" )
                .collect( Collectors.toList() ) );
        distanceErrors.addAll( checkTravelMeanEnum( new ArrayList <>( distanceConstraints ) ) );

        return distanceErrors;
    }

    private List < String > checkTravelMeanEnum ( List < AddConstraintMessage > constraintMessages ) {
        List < String > errors = new ArrayList <>();
        errors.addAll( constraintMessages.stream()
                .filter( constraint -> !TravelMeanEnum.isValid( constraint.getConcerns() ) )
                .map( constraint -> constraint.getClass().getSimpleName() + " " +
                        constraintMessages.indexOf( constraint ) +
                        " travel mean value not allowed" )
                .collect( Collectors.toList() ) );
        return errors;
    }

    private TypeOfEvent createTypeOfEvent ( AddTypeOfEventMessage message ) {
        TypeOfEvent typeOfEvent = new TypeOfEvent( message.getName(), message.getParamFirstPath() );
        typeOfEvent.setDeactivate( message.getDeactivate() );
        ArrayList < Constraint > constraints = new ArrayList <>();

        for ( AddDistanceConstraintMessage distanceLimit : message.getLimitedByDistance() ) {
            constraints.add( new DistanceConstraint( distanceLimit.getConcerns(),
                    distanceLimit.getMinLength(), distanceLimit.getMaxLength() ) );
        }
        for ( AddPeriodConstraintMessage periodLimit : message.getLimitedByPeriod() ) {
            constraints.add( new PeriodOfDayConstraint( periodLimit.getConcerns(),
                    periodLimit.getMinHour(), periodLimit.getMaxHour() ) );
        }
        typeOfEvent.setLimitedBy( constraints );

        return typeOfEvent;
    }


    //PREFERRED LOCATIONS

    public List < UserLocation > getAllPreferredLocations () {
        return new ArrayList < UserLocation >( currentUser.getPreferredLocations() );
    }

    public UserLocation getPreferredLocation ( String name ) throws EntityNotFoundException {
        List < UserLocation > preferredLocations = currentUser.getPreferredLocations();

        UserLocation requested = preferredLocations.stream()
                .filter( userLocation -> userLocation.getName().equals( name ) )
                .findFirst().orElse( null );
        if ( requested == null ) {
            throw new EntityNotFoundException();
        }
        return requested;
    }

    public void addPreferredLocation ( PreferredLocationMessage locationMessage ) throws InvalidFieldException {
        checkLocationConsistency( locationMessage );

        Location location = new Location( locationMessage.getLatitude(), locationMessage.getLongitude(),
                locationMessage.getAddress() );
        location.save();
        currentUser.addLocation( locationMessage.getName(), location );
        currentUser.save();
    }

    private void checkLocationConsistency ( PreferredLocationMessage locationMessage ) throws InvalidFieldException {
        List < String > errors = new ArrayList <>();
        if ( locationMessage.getName() == null ) {
            errors.add( "name" );
        }
        if ( locationMessage.getAddress() == null ) {
            errors.add( "address" );
        }
        //TODO ask to external service if address is correct?

        if ( errors.size() > 0 ) {
            throw new InvalidFieldException( errors );
        }
    }

    public void modifyPreferredLocation ( PreferredLocationMessage locationMessage )
            throws InvalidFieldException, EntityNotFoundException {

        checkLocationConsistency( locationMessage );
        currentUser.getPreferredLocations();
        deletePreferredLocation( locationMessage.getName() );
        addPreferredLocation( locationMessage );
    }

    public void deletePreferredLocation ( String name ) throws EntityNotFoundException {
        UserLocation location = getPreferredLocation( name );
        currentUser.removeLocation( name );
        currentUser.save();
    }

    public boolean checkConstraints ( Travel travel, TypeOfEvent typeOfEvent ) {
        for ( TravelComponent travelComponent : travel.getMiniTravels() ) {
            TravelMeanEnum travelMean = travelComponent.getMeanUsed().getType();
            if ( typeOfEvent.isDeactivated( travelMean ) ) {
                return false;
            }
            ArrayList < Constraint > consList = typeOfEvent.getLimitedBy( travelMean );
            for ( Constraint constraint : consList ) {
                if ( !constraint.respectConstraint( travelComponent ) ) {
                    return false;
                }
            }
        }
        return true;
    }

    public PathCombination findBestpath ( ArrayList < PathCombination > combs, TypeOfEvent typeOfEvent ) {
        if ( typeOfEvent.getParamFirstPath() != null ) {
            switch ( typeOfEvent.getParamFirstPath() ) {
                case MIN_LENGTH:
                    return getPathsWithMinLength( combs );
                case MIN_TIME:
                    return getPathsWithMinTime( combs );
                default:
                    return combs.get( 0 ); //TODO cost and eco cases
            }
        } else
            return combs.get( 0 );
    }

    private PathCombination getPathsWithMinLength ( ArrayList < PathCombination > combs ) {
        PathCombination best = ( combs != null ) ? combs.get( 0 ) : null;
        for ( PathCombination singleComb : combs )
            if ( singleComb.getTotalLength() < best.getTotalLength() )
                best = singleComb;
        return best;
    }

    private PathCombination getPathsWithMinTime ( ArrayList < PathCombination > combs ) {
        PathCombination best = ( combs != null ) ? combs.get( 0 ) : null;
        for ( PathCombination singleComb : combs )
            if ( singleComb.getTotalTime() < best.getTotalTime() )
                best = singleComb;
        return best;
    }

    public ArrayList < TravelMeanEnum > getAllowedMeans ( Event event, TravelMeanEnum[] list ) {
        ArrayList < TravelMeanEnum > privateMeans = new ArrayList < TravelMeanEnum >();
        for ( TravelMeanEnum mean : list )
            if ( isVehicleAllowed( event, mean ) )
                privateMeans.add( mean );
        return privateMeans;
    }

    protected boolean isVehicleAllowed ( Event event, TravelMeanEnum vehicle ) {
        return !event.getType().isDeactivated( vehicle );
    }

}
