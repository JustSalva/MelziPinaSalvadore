package it.polimi.travlendarplus.exceptions.googleMapsExceptions;

public class LocationNotFoundException extends GMapsGeneralException {

    private static final long serialVersionUID = -6359585524936093709L;

    public LocationNotFoundException() {
        super( "Location not found! Please specify a correct location." );
    }

    public LocationNotFoundException( String message ) {
        super( message );
    }

}
