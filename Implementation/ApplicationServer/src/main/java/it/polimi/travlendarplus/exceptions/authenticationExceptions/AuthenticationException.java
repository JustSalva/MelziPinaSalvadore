package it.polimi.travlendarplus.exceptions.authenticationExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

/**
 * This is the main exception class used during the authentication process.
 * It is to be extended by all others authentication exceptions
 */
public class AuthenticationException extends TravlendarPlusException{

    private static final long serialVersionUID = -7319835705869708486L;

    public AuthenticationException() {
    }

    public AuthenticationException( String message ) {
        super( message );
    }
}
