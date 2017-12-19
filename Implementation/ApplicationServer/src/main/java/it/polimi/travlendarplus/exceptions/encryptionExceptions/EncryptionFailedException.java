package it.polimi.travlendarplus.exceptions.encryptionExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

public class EncryptionFailedException extends TravlendarPlusException {

    private static final long serialVersionUID = 4761687530528543428L;

    public EncryptionFailedException () {
    }

    public EncryptionFailedException ( String message ) {
        super( message );
    }
}
