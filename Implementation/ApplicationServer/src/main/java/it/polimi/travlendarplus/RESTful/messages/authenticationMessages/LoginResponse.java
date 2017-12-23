package it.polimi.travlendarplus.RESTful.messages.authenticationMessages;

/**
 * Response message used to reply to the users after a login operation
 */
public class LoginResponse extends TokenResponse {

    private static final long serialVersionUID = -3466960414847612423L;

    private String name;
    private String surname;

    public LoginResponse () {
    }

    public LoginResponse ( String token, String name, String surname ) {
        super( token );
        this.name = name;
        this.surname = surname;
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public String getSurname () {
        return surname;
    }

    public void setSurname ( String surname ) {
        this.surname = surname;
    }
}
