package it.polimi.travlendarplus.exceptions.tripManagerExceptions;

public class IncompatibleTravelMeansException extends TripManagerException {

    private static final long serialVersionUID = -3680647586373638927L;

    public IncompatibleTravelMeansException () {
        super ( "incompatible travel means" );
    }

    public IncompatibleTravelMeansException ( String message ) {
        super( message );
    }
}
