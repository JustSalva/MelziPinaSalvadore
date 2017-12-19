package it.polimi.travlendarplus.exceptions.persistenceExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

/**
 * This exception is thrown when an load instruction is performed
 * using a non existent primary key
 */
public class EntityNotFoundException extends TravlendarPlusException {

    private static final long serialVersionUID = -1665060021863456498L;

    public EntityNotFoundException () {
    }

    public EntityNotFoundException ( String message ) {
        super( message );
    }
}
