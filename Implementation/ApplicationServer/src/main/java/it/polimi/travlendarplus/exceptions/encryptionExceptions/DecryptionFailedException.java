package it.polimi.travlendarplus.exceptions.encryptionExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

public class DecryptionFailedException extends TravlendarPlusException {

    private static final long serialVersionUID = 6756318588777639095L;

    public DecryptionFailedException () {
        super( "Decryption Failed Exception thrown - failure" );
    }

    public DecryptionFailedException ( String message ) {
        super( message );
    }
}
