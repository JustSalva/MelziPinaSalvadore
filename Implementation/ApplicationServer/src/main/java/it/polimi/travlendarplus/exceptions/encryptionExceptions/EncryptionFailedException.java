package it.polimi.travlendarplus.exceptions.encryptionExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

/**
 * This exception is thrown when the encryption of a password fails
 */
public class EncryptionFailedException extends TravlendarPlusException {

    private static final long serialVersionUID = 4761687530528543428L;

    public EncryptionFailedException () {
    }

    public EncryptionFailedException ( String message ) {
        super( message );
    }
}
