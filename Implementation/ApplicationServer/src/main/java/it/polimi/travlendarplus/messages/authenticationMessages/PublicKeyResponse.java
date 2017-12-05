package it.polimi.travlendarplus.messages.authenticationMessages;

/**
 * Response message used to reply to the users after an public key request
 * with the public key associated with the device the request come from.
 */
public class PublicKeyResponse extends ResponseMessage{

    private static final long serialVersionUID = -8187045248923272954L;

    private String publicKey;

    public PublicKeyResponse() {
    }

    public PublicKeyResponse( String publicKey ) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey( String publicKey ) {
        this.publicKey = publicKey;
    }
}
