package it.polimi.travlendarplus.exceptions.persistenceExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

public class EntityNotFoundException extends TravlendarPlusException {

    private static final long serialVersionUID = -1665060021863456498L;

    public EntityNotFoundException () {
    }

    public EntityNotFoundException ( String message ) {
        super( message );
    }
}
