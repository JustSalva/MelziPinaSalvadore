package it.polimi.travlendarplus.exceptions.googleMapsExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

/**
 * This exception is the most generic exception in googleMapsUtilities package,
 * it is to be extended by all exceptions related to googleMapsUtilities functionalities
 */
public class GMapsGeneralException extends TravlendarPlusException {

    private static final long serialVersionUID = -3422677194942928823L;

    public GMapsGeneralException () {

    }

    public GMapsGeneralException ( String message ) {
        super( message );
    }
}
