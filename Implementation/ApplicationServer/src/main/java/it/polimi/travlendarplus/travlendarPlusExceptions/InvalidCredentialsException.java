package it.polimi.travlendarplus.travlendarPlusExceptions;

/**
 * This exception is thrown during the authentication process,
 * if the credential sent to the ApplicationServer are invalid.
 */
public class InvalidCredentialsException extends TravlendarPlusException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
