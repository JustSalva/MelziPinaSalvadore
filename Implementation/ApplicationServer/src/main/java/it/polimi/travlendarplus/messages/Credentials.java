package it.polimi.travlendarplus.messages;

/**
 * Message to be sent by a client to perform a login
 */
public class Credentials extends GenericMessage {

    private String username;
    private String password;
    private String idDevice;

    public Credentials() {
    }

    public Credentials(String username, String password, String idDevice) {
        this.username = username;
        this.password = password;
        this.idDevice = idDevice;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(String idDevice) {
        this.idDevice = idDevice;
    }
}