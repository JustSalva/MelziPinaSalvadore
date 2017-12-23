package it.polimi.travlendarplus.exceptions.googleMapsExceptions;

import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.WrongFields;

/**
 * This exception is thrown when the specified location values are inconsistent
 */
public class LocationNotFoundException extends GMapsGeneralException {

    private static final long serialVersionUID = -6359585524936093709L;

    public LocationNotFoundException () {
        super( WrongFields.LOCATION_NOT_FOUND );
    }

    public LocationNotFoundException ( String message ) {
        super( message );
    }

}
