package it.polimi.travlendarplus.exceptions.encryptionExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

public class DecryprionFailedException extends TravlendarPlusException {

    private static final long serialVersionUID = 6756318588777639095L;

    public DecryprionFailedException() {
    }

    public DecryprionFailedException( String message ) {
        super( message );
    }
}
