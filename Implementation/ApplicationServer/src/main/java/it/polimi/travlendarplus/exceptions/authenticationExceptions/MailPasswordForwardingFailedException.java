package it.polimi.travlendarplus.exceptions.authenticationExceptions;

/**
 * This exception can be thrown if the email forwarding fails
 */
public class MailPasswordForwardingFailedException extends AuthenticationException {

    private static final long serialVersionUID = -2057701012575125049L;

    public MailPasswordForwardingFailedException () {
    }

    public MailPasswordForwardingFailedException ( String message ) {
        super( message );
    }
}
