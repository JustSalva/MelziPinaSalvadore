package it.polimi.travlendarplus.exceptions.tripManagerExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

/**
 * This exception is the most generic exception used in tripManager package,
 * it is to be extended by all exceptions related to tripManager functionalities
 */
public class TripManagerException extends TravlendarPlusException {

    private static final long serialVersionUID = 824264771933410008L;

    public TripManagerException () {
    }

    public TripManagerException ( String message ) {
        super( message );
    }
}
