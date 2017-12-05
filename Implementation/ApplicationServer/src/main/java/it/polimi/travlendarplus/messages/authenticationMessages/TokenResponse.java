package it.polimi.travlendarplus.messages.authenticationMessages;


/**
 * Response message used to reply to the users after an authentication-related request
 * with the univocal code associated with the device the request come from.
 */
public class TokenResponse extends ResponseMessage{

    private static final long serialVersionUID = -574173175513252018L;

    private String token;

    public TokenResponse() {
    }

    public TokenResponse( String token ) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken( String token ) {
        this.token = token;
    }
}
