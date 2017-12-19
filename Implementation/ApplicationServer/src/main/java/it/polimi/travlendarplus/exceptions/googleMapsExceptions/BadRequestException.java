package it.polimi.travlendarplus.exceptions.googleMapsExceptions;

/**
 * This exception is thrown when an invalid request is submitted to Google maps services
 */
public class BadRequestException extends GMapsGeneralException {

    private static final long serialVersionUID = -2319824472426978187L;

    public BadRequestException () {
        super( "Sorry! This request can't be performed." );
    }

    public BadRequestException ( String message ) {
        super( message );
    }

}
