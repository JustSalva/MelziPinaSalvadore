package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages.PreferredLocationMessage;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class PreferenceManager extends UserManager{

    public TypeOfEvent getPreferencesProfiles() {
        //currentUser.getPreferences().stream()
        return null;
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
