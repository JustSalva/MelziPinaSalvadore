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
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.WrongFields;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provide all methods related to handle the user preferences
 */
@Stateless
public class PreferenceManager extends UserManager {

    private final float MAX_WALKING_LENGTH_ALWAYS_ACCEPTED = 0.5f;

    /**
     * Provides all preferences profiles of the authenticated user
     *
     * @return the requested profiles
     */
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

    /**
     * Adds a preference profile into the users account
     *
     * @param typeOfEventMessage message representing the user's profile to be added
     * @return the added preference profile, comprehensive of his id
     * @throws InvalidFieldException if some fields of the message are wrong, which one is specified inside the error
     */
    public TypeOfEvent addTypeOfEvent ( AddTypeOfEventMessage typeOfEventMessage ) throws InvalidFieldException {
        checkTypeOfEventConsistency( typeOfEventMessage );
        TypeOfEvent typeOfEvent = createTypeOfEvent( typeOfEventMessage );
        typeOfEvent.save();
        currentUser.addPreference( typeOfEvent );
        currentUser.save();
        return typeOfEvent;
    }

    /**
     * Modifies an already existent preference profile
     *
     * @param typeOfEventMessage message representing how the user's profile is to be modified
     * @return the modified preference profile, comprehensive of his new id
     * @throws InvalidFieldException   if some fields of the message are wrong, which one is specified inside the error
     * @throws EntityNotFoundException if the profile to be modified does not exists
     */
    public TypeOfEvent modifyTypeOfEvent ( ModifyTypeOfEventMessage typeOfEventMessage )
            throws InvalidFieldException, EntityNotFoundException {
        //checks the event existence
        getPreferencesProfile( typeOfEventMessage.getId() );
        checkTypeOfEventConsistency( typeOfEventMessage );
        deleteTypeOfEvent( typeOfEventMessage.getId() );
        return addTypeOfEvent( typeOfEventMessage );

    }

    /**
     * Deletes an already existent preference profile
     *
     * @param id identifier of the preference profile
     * @throws EntityNotFoundException if the profile to be modified does not exists
     */
    public void deleteTypeOfEvent ( long id ) throws EntityNotFoundException {
        // NB if the type of event is still used in some events it will remain saved into them
        getPreferencesProfile( id );
        currentUser.removePreference( id );
        currentUser.save();
    }

    /**
     * Checks that a type of event message info are correct
     *
     * @param typeOfEventMessage message representing a type of event
     * @throws InvalidFieldException if some fields of the message are wrong, which one is specified inside the error
     */
    private void checkTypeOfEventConsistency ( AddTypeOfEventMessage typeOfEventMessage ) throws InvalidFieldException {
        List < String > errors = new ArrayList <>();
        if ( typeOfEventMessage.getName() == null ) {
            errors.add( WrongFields.NAME );
        }
        if ( typeOfEventMessage.getParamFirstPath() == null ) {
            errors.add( WrongFields.PARAM_FIRST_PATH );
        }

        errors.addAll( checkPeriodConstraints( typeOfEventMessage.getLimitedByPeriod() ) );
        errors.addAll( checkDistanceConstraints( typeOfEventMessage.getLimitedByDistance() ) );

        if ( errors.size() > 0 ) {
            throw new InvalidFieldException( errors );
        }
    }

    /**
     * Checks that the period constraints info inside a list of period constraint messages are correct
     *
     * @param periodConstraints message representing a period constraints
     * @return a list of wrong fields if there are some, an empty list otherwise
     */
    private List < String > checkPeriodConstraints ( List < AddPeriodConstraintMessage > periodConstraints ) {
        List < String > periodErrors = new ArrayList <>();
        //checks that min < max hour
        periodErrors.addAll( periodConstraints.stream()
                .filter( periodConstraint -> periodConstraint.getMinHour() > periodConstraint.getMaxHour() )
                .map( periodConstraint -> WrongFields.PERIOD_CONSTRAINT +
                        periodConstraints.indexOf( periodConstraint ) +
                        WrongFields.MIN_HOUR_MUST_BE_LESS_THAN_MAX_HOUR )
                .collect( Collectors.toCollection( ArrayList::new ) ) );
        //checks that minHour >= 0
        periodErrors.addAll( periodConstraints.stream()
                .filter( periodConstraint -> periodConstraint.getMinHour() < 0 )
                .map( periodConstraint -> WrongFields.PERIOD_CONSTRAINT +
                        periodConstraints.indexOf( periodConstraint ) +
                        WrongFields.MIN_HOUR_MUST_BE_GREATER_THAN_ZERO )
                .collect( Collectors.toCollection( ArrayList::new ) ) );
        //checks that maxHour < 24 h
        periodErrors.addAll( periodConstraints.stream()
                .filter( periodConstraint -> periodConstraint.getMaxHour() >= 24 * 60 * 60 )
                .map( periodConstraint -> WrongFields.PERIOD_CONSTRAINT +
                        periodConstraints.indexOf( periodConstraint ) +
                        WrongFields.MAX_HOUR_MUST_BE_LESS_THAN_24_HOURS )
                .collect( Collectors.toCollection( ArrayList::new ) ) );
        periodErrors.addAll( checkTravelMeanEnum( new ArrayList <>( periodConstraints ) ) );

        return periodErrors;
    }

    /**
     * Checks that the distance constraint info inside a list of distance constraint messages
     *
     * @param distanceConstraints message representing a period constraints
     * @return a list of wrong fields if there are some, an empty list otherwise
     */
    private List < String > checkDistanceConstraints ( List < AddDistanceConstraintMessage > distanceConstraints ) {
        List < String > distanceErrors = new ArrayList <>();
        //checks that min length >= 0
        distanceErrors.addAll( distanceConstraints.stream()
                .filter( distanceConstraint -> distanceConstraint.getMinLength() < 0 )
                .map( distanceConstraint -> WrongFields.DISTANCE_CONSTRAINT +
                        distanceConstraints.indexOf( distanceConstraint ) +
                        WrongFields.MIN_LENGTH_MUST_BE_GREATER_THAN_ZERO )
                .collect( Collectors.toCollection( ArrayList::new ) ) );
        //checks that min length < max length
        distanceErrors.addAll( distanceConstraints.stream()
                .filter( distanceConstraint -> distanceConstraint.getMinLength() > distanceConstraint.getMaxLength() )
                .map( distanceConstraint -> WrongFields.DISTANCE_CONSTRAINT +
                        distanceConstraints.indexOf( distanceConstraint ) +
                        WrongFields.MIN_LENGTH_MUST_BE_LESS_THAN_MAX_LENGHT )
                .collect( Collectors.toCollection( ArrayList::new ) ) );
        distanceErrors.addAll( checkTravelMeanEnum( new ArrayList <>( distanceConstraints ) ) );

        return distanceErrors;
    }

    /**
     * Checks the consistency of a travel mean enum list inside a list of constraint messages
     *
     * @param constraintMessages list of messages containing the constraints whose travel mean enum is to be checked
     * @return a list of wrong fields if there are some, an empty list otherwise
     */
    private List < String > checkTravelMeanEnum ( List < AddConstraintMessage > constraintMessages ) {
        List < String > errors = new ArrayList <>();
        errors.addAll( constraintMessages.stream()
                .filter( constraint -> !TravelMeanEnum.isValid( constraint.getConcerns() ) )
                .map( constraint -> constraint.getClass().getSimpleName() + " " +
                        constraintMessages.indexOf( constraint ) +
                        WrongFields.NOT_ALLOWED_TRAVEL_MEAN_VALUE )
                .collect( Collectors.toCollection( ArrayList::new ) ) );
        return errors;
    }

    /**
     * Creates an instance of type of event, given his relative message
     *
     * @param message contains all info to be saved into a new type of event
     * @return the requested type of event
     */
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

    /**
     * Allows to retrieve all the preferred locations of the authenticated user
     *
     * @return the requested locations
     */
    public List < UserLocation > getAllPreferredLocations () {
        return new ArrayList < UserLocation >( currentUser.getPreferredLocations() );
    }

    /**
     * Allows to retrieve a specific location of the authenticated user
     *
     * @param name identifier of the requested location
     * @return the requested location
     * @throws EntityNotFoundException if the location does not exists in the user's profile
     */
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

    /**
     * Adds a preferred location into the user's profile
     *
     * @param locationMessage message containing info about the location to be added
     * @throws InvalidFieldException if some message's fields are inconsistent
     */
    public void addPreferredLocation ( PreferredLocationMessage locationMessage ) throws InvalidFieldException {
        checkLocationConsistency( locationMessage );

        Location location = new Location( locationMessage.getLatitude(), locationMessage.getLongitude(),
                locationMessage.getAddress() );
        location.save();
        currentUser.addLocation( locationMessage.getName(), location );
        currentUser.save();
    }

    /**
     * Checks that the location message fields are consistent
     *
     * @param locationMessage message containing info about the location to be added
     * @throws InvalidFieldException if some message's fields are inconsistent
     */
    private void checkLocationConsistency ( PreferredLocationMessage locationMessage ) throws InvalidFieldException {
        List < String > errors = new ArrayList <>();
        if ( locationMessage.getName() == null ) {
            errors.add( WrongFields.NAME );
        }
        if ( locationMessage.getAddress() == null ) {
            errors.add( WrongFields.ADDRESS );
        }

        if ( errors.size() > 0 ) {
            throw new InvalidFieldException( errors );
        }
    }

    /**
     * Modifies a user's preferred location
     *
     * @param locationMessage message containing info about the location to be modified
     * @throws InvalidFieldException   if some message's fields are inconsistent
     * @throws EntityNotFoundException if the location, whose change is requested, does not exist
     */
    public void modifyPreferredLocation ( PreferredLocationMessage locationMessage )
            throws InvalidFieldException, EntityNotFoundException {

        checkLocationConsistency( locationMessage );
        currentUser.getPreferredLocations();
        deletePreferredLocation( locationMessage.getName() );
        addPreferredLocation( locationMessage );
    }

    /**
     * Deletes a preferred location from the user's profile
     *
     * @param name identifier of the requested location
     * @throws EntityNotFoundException if the location, whose delete operation is requested, does not exist
     */
    public void deletePreferredLocation ( String name ) throws EntityNotFoundException {
        getPreferredLocation( name );
        currentUser.removeLocation( name );
        currentUser.save();
    }

    /**
     * Checks that that a travel respect the constraints of a type of event
     *
     * @param travel      instance of the travel to be checked
     * @param typeOfEvent instance of the type of event on which the control is to be performed
     * @return true if the travel respect those constraints, false otherwise
     */
    public boolean checkConstraints ( Travel travel, TypeOfEvent typeOfEvent ) {
        for ( TravelComponent travelComponent : travel.getMiniTravels() ) {
            TravelMeanEnum travelMean = travelComponent.getMeanUsed().getType();
            if ( travelMean.getParam().equals( "walking" ) && travelComponent.getLength() <=
                    MAX_WALKING_LENGTH_ALWAYS_ACCEPTED ) {
                // A walking-component with a short distance cannot cause the exclusion of a path,
                // also if BY_FOOT is deactivated.
            } else if ( typeOfEvent.isDeactivated( travelMean ) ) {
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

    /**
     * Selects from a list of proposed travel paths the best one according to the user's preferences
     *
     * @param combs       list of proposed travel paths
     * @param typeOfEvent user preference that will determine which path is the best
     * @return the best path from the ones in the provided list
     */
    public PathCombination findBestPath ( List < PathCombination > combs, TypeOfEvent typeOfEvent ) {
        if ( typeOfEvent.getParamFirstPath() != null ) {
            switch ( typeOfEvent.getParamFirstPath() ) {
                case MIN_LENGTH:
                    return getPathsWithMinLength( combs );
                case MIN_TIME:
                    return getPathsWithMinTime( combs );
                //TODO cost and eco cases in successive release
                /*case MIN_COST:
                    return getPathsWithMinTime( combs );
                case ECO_PATH:
                    return getPathsWithMinTime( combs );*/
                default:
                    return combs.get( 0 );
            }
        } else {
            return combs.get( 0 );
        }
    }

    /**
     * Retrieve the travel path with less distance traveled from those in the provided list
     *
     * @param combs list of travel paths
     * @return the travel path with less distance traveled
     */
    private PathCombination getPathsWithMinLength ( List < PathCombination > combs ) {
        if ( combs == null ) {
            return null;
        }
        PathCombination best = combs.get( 0 );
        for ( PathCombination singleComb : combs ) {
            if ( singleComb.getTotalLength() < best.getTotalLength() ) {
                best = singleComb;
            }
        }
        return best;
    }

    /**
     * Retrieve the travel path with less travel time from those in the provided list
     *
     * @param combs list of travel paths
     * @return the travel path with less travel time
     */
    private PathCombination getPathsWithMinTime ( List < PathCombination > combs ) {
        if ( combs == null ) {
            return null;
        }
        PathCombination best = combs.get( 0 );
        for ( PathCombination singleComb : combs ) {
            if ( singleComb.getTotalTime() < best.getTotalTime() ) {
                best = singleComb;
            }
        }
        return best;
    }

    /**
     * Provide the travel means, from those contained in the specified list,
     * that are allowed in the event's preferences
     *
     * @param event event whose preferences are to be checked
     * @param list  travel means that are to be checked
     * @return a list of allowed travel means
     */
    public List < TravelMeanEnum > getAllowedMeans ( Event event, TravelMeanEnum[] list ) {
        ArrayList < TravelMeanEnum > privateMeans = new ArrayList < TravelMeanEnum >();
        for ( TravelMeanEnum mean : list ) {
            if ( isVehicleAllowed( event, mean ) ) {
                privateMeans.add( mean );
            }
        }
        return privateMeans;
    }

    /**
     * Checks if a travel mean is allowed according to an event preferences
     *
     * @param event   event whose preferences are to be checked
     * @param vehicle travel means to be checked
     * @return true if the specified travel mean is allowed, false otherwise
     */
    private boolean isVehicleAllowed ( Event event, TravelMeanEnum vehicle ) {
        return !event.getType().isDeactivated( vehicle );
    }

}
