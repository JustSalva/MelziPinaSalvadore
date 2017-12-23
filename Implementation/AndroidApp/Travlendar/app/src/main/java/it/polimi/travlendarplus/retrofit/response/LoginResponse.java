package it.polimi.travlendarplus.retrofit.response;

/**
 * Login info parsed from the JSON returned by the server.
 */
public class LoginResponse {

    private String token;
    private String name;
    private String surname;

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
