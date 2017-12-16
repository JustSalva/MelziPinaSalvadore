package it.polimi.travlendarplus.exceptions.encryptionExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

public class EncryprionFailedException extends TravlendarPlusException {

    private static final long serialVersionUID = 4761687530528543428L;

    public EncryprionFailedException () {
    }

    public EncryprionFailedException ( String message ) {
        super( message );
    }
}
