package it.polimi.travlendarplus.exceptions.authenticationExceptions;

/**
 * This exception is thrown during the authentication process,
 * if the user who is trying to register himself is already registered.
 */
public class UserAlreadyRegisteredException extends AuthenticationException {

    private static final long serialVersionUID = -1255875692972274983L;

    public UserAlreadyRegisteredException() {
    }

    public UserAlreadyRegisteredException( String message ) {
        super( message );
    }
}
