package it.polimi.travlendarplus.exceptions.googleMapsExceptions;

/**
 * This exception is thrown when Google maps services are unavailable to the application server
 */
public class GMapsUnavailableException extends GMapsGeneralException {

    private static final long serialVersionUID = 4651589841187375677L;

    public GMapsUnavailableException () {
    }

    public GMapsUnavailableException ( String message ) {
        super( message );
    }
}
