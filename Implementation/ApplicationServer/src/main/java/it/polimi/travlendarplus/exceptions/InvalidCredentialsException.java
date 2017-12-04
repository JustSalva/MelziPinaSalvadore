package it.polimi.travlendarplus.exceptions;

/**
 * This exception is thrown during the authentication process,
 * if the credential sent to the ApplicationServer are invalid.
 */
public class InvalidCredentialsException extends TravlendarPlusException {

    private static final long serialVersionUID = 8479223991838275408L;

    public InvalidCredentialsException() {
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
