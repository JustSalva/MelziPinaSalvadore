package it.polimi.travlendarplus.exceptions.googleMapsExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

public abstract class GMapsGeneralException extends TravlendarPlusException {

    private static final long serialVersionUID = -3422677194942928823L;

    public GMapsGeneralException () {

    }

    public GMapsGeneralException ( String message ) {
        super( message );
    }
}
