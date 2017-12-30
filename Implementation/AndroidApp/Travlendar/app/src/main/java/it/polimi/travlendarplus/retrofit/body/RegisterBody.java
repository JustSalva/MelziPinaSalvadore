package it.polimi.travlendarplus.retrofit.body;


/**
 * Body to be sent to server to perform a registration.
 */
public class RegisterBody {

    private String email;
    private String password;
    private String idDevice;
    private String name;
    private String surname;

    public RegisterBody ( String email, String password, String idDevice, String name, String surname ) {
        this.email = email;
        this.password = password;
        this.idDevice = idDevice;
        this.name = name;
        this.surname = surname;
    }
}
