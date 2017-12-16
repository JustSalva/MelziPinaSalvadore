package it.polimi.travlendarplus.exceptions.googleMapsExceptions;

public class BadRequestException extends GMapsGeneralException {

    private static final long serialVersionUID = -2319824472426978187L;

    public BadRequestException () {
        super( "Sorry! This request can't be performed." );
    }

    public BadRequestException ( String message ) {
        super( message );
    }

}
