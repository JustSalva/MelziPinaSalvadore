package it.polimi.travlendarplus.beans.calendar_manager.support.GMapsException;

public class LocationNotFoundException extends GMapsGeneralException {

    public LocationNotFoundException() {
        super("Location not found! Please specify a correct location.");
    }

    public LocationNotFoundException(String message) {
        super(message);
    }

}
