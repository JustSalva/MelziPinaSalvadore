package it.polimi.travlendarplus.messages.authenticationMessages;

/**
 *  Message to be sent by a client to request a public key in order to encrypt his login or registration message
 *  it contains the id of the device from which the user wants to log in
 */
public class IdDeviceMessage extends AuthenticationMessage {

    private static final long serialVersionUID = 2274766363979248213L;

    private String idDevice;

    public IdDeviceMessage() {
    }

    public IdDeviceMessage( String idDevice ) {
        this.idDevice = idDevice;
    }

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice( String idDevice ) {
        this.idDevice = idDevice;
    }
}
