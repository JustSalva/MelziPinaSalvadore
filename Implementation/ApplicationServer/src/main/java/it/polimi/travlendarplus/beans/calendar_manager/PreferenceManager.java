package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.preferences.Constraint;
import it.polimi.travlendarplus.entities.preferences.DistanceConstraint;
import it.polimi.travlendarplus.entities.preferences.PeriodOfDayConstraint;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages.*;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class PreferenceManager extends UserManager{

    public List<TypeOfEvent> getPreferencesProfiles() {
        return currentUser.getPreferences();
    }

    public TypeOfEvent getPreferencesProfile( long id ) throws EntityNotFoundException{
        List<TypeOfEvent> profiles = getPreferencesProfiles();
        TypeOfEvent requested = profiles.stream()
                .filter( typeOfEvent -> typeOfEvent.getId() == id )
                .findFirst().orElse( null );
        if ( requested == null){
            throw new EntityNotFoundException(  );
        }
        return requested;
    }

    public TypeOfEvent addTypeOfEvent( AddTypeOfEventMessage typeOfEventMessage ) throws InvalidFieldException{
        checkTypeOfEventConsistency( typeOfEventMessage );
        TypeOfEvent typeOfEvent = createTypeOfEvent( typeOfEventMessage );
        typeOfEvent.save();
        currentUser.addPreference( typeOfEvent );
        currentUser.save();
        return typeOfEvent;
    }

    public TypeOfEvent modifyTypeOfEvent( ModifyTypeOfEventMessage typeOfEventMessage ) throws InvalidFieldException, EntityNotFoundException{
        deleteTypeOfEvent( typeOfEventMessage.getId() );
        return addTypeOfEvent( typeOfEventMessage );
    }

    public void deleteTypeOfEvent( long id ) throws EntityNotFoundException{
        TypeOfEvent typeOfEvent = getPreferencesProfile( id );
        typeOfEvent.remove();
    }

    private void checkTypeOfEventConsistency( AddTypeOfEventMessage typeOfEventMessage ) throws InvalidFieldException {
        //TODO write also in error which fields are wrong
    }

    private TypeOfEvent createTypeOfEvent( AddTypeOfEventMessage message ){
        TypeOfEvent typeOfEvent = new TypeOfEvent( message.getName(), message.getParamFirstPath() );
        typeOfEvent.setDeactivate( message.getDeactivate() );
        ArrayList< Constraint > constraints = new ArrayList<>();

        for(AddDistanceConstraintMessage distanceLimit : message.getLimitedByDistance() ){
            constraints.add( new DistanceConstraint( distanceLimit.getConcerns(),
                    distanceLimit.getMinLenght(), distanceLimit.getMaxLenght() ) );
        }
        for(AddPeriodConstraintMessage periodLimit : message.getLimitedByPeriod() ){
            constraints.add( new PeriodOfDayConstraint( periodLimit.getConcerns(),
                    periodLimit.getMinHour(), periodLimit.getMaxHour() ) );
        }
        typeOfEvent.setLimitedBy( constraints );

        return typeOfEvent;
    }


    //PREFERRED LOCATIONS

    public Map<Location, String> getAllPreferredLocations(){
        return new HashMap<>( currentUser.getPreferredLocations() );
    }

    public Location getPreferredLocation( String name ) throws EntityNotFoundException{
        Map<Location, String> preferredLocations = currentUser.getPreferredLocations();
        for (Map.Entry<Location, String> entry : preferredLocations.entrySet()) {
            if(entry.getValue().equals( name )) {
                return entry.getKey();
            }
        }
        throw new EntityNotFoundException();
    }

    public void addPreferredLocation( PreferredLocationMessage locationMessage ) throws InvalidFieldException {
        checkLocationConsistency( locationMessage );
        Location location = new Location( locationMessage.getLatitude(), locationMessage.getLongitude(),
                locationMessage.getAddress() );
        location.save();
        currentUser.addLocation( locationMessage.getName(), location );
        currentUser.save();
    }

    private void checkLocationConsistency( PreferredLocationMessage locationMessage ) throws InvalidFieldException {
        //TODO write also in error which fields are wrong
    }

    public void modifyPreferredLocation( PreferredLocationMessage locationMessage ) throws InvalidFieldException, EntityNotFoundException{
        checkLocationConsistency( locationMessage );
        Map<Location, String> preferredLocations = currentUser.getPreferredLocations();
        Location location = null;
        for (Map.Entry<Location, String> entry : preferredLocations.entrySet()) {
            if(entry.getValue().equals( locationMessage.getName() )){
                location= entry.getKey();
                currentUser.removeLocation( location );
                currentUser.save();
                break;
            }

        }
        if( location == null){
            throw new EntityNotFoundException();
        }
        addPreferredLocation( locationMessage );
    }

    public void deletePreferredLocation( String name ) throws EntityNotFoundException{
        getPreferredLocation( name ).remove();
    }


}
