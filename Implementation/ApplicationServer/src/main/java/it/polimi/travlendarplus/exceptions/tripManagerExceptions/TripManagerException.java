package it.polimi.travlendarplus.exceptions.tripManagerExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

public class TripManagerException extends TravlendarPlusException {

    private static final long serialVersionUID = 824264771933410008L;

    public TripManagerException () {
    }

    public TripManagerException ( String message ) {
        super( message );
    }
}
