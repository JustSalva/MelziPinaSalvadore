package it.polimi.travlendarplus.messages.authenticationMessages;

import java.security.PublicKey;

/**
 * Response message used to reply to the users after an public key request
 * with the public key associated with the device the request come from.
 */
public class PublicKeyResponse extends ResponseMessage{

    private static final long serialVersionUID = -8187045248923272954L;

    private PublicKey publicKey;

    public PublicKeyResponse() {
    }

    public PublicKeyResponse( PublicKey publicKey ) {
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey( PublicKey publicKey ) {
        this.publicKey = publicKey;
    }
}
