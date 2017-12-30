package it.polimi.travlendarplus.retrofit.body;


/**
 * Body to be sent to server to perform a login.
 * It contains the user's info.
 */
public class LoginBody {

    private String email;
    private String password;
    private String idDevice;

    public LoginBody ( String email, String password, String idDevice ) {
        this.email = email;
        this.password = password;
        this.idDevice = idDevice;
    }
}
