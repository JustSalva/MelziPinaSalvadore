package it.polimi.travlendarplus.exceptions.authenticationExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

/**
 * This exception is thrown during the token checking process
 * when the user sends a request with a token and that token is invalid
 */
public class InvalidTokenException extends TravlendarPlusException {

    private static final long serialVersionUID = -3276042348718545662L;

    public InvalidTokenException() {
    }

    public InvalidTokenException( String message ) {
        super( message );
    }
}
