package it.polimi.travlendarplus.exceptions.authenticationExceptions;

/**
 * This exception is thrown during the authentication process,
 * if the credential sent to the ApplicationServer are not associated to an user.
 */
public class UserNotRegisteredException extends AuthenticationException {

    private static final long serialVersionUID = 3675655546635692167L;

    public UserNotRegisteredException() {
    }

    public UserNotRegisteredException(String message) {
        super(message);
    }
}
