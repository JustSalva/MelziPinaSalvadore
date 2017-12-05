package it.polimi.travlendarplus.messages.authenticationMessages;

import it.polimi.travlendarplus.messages.authenticationMessages.AuthenticationMessage;

/**
 * Message to be sent by a client to perform a login
 */
public class Credentials extends AuthenticationMessage {

    private static final long serialVersionUID = 4386269294697577939L;

    //TODO encryption!!!
    private String email;
    private String password;
    private String idDevice;

    public Credentials() {
    }

    public Credentials(String email, String password, String idDevice) {
        this.email = email;
        this.password = password;
        this.idDevice = idDevice;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return "Credentials{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", idDevice='" + idDevice + '\'' +
                '}';
    }
}