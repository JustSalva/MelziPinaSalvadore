package it.polimi.travlendarplus.RESTful.messages.authenticationMessages;

import java.security.PublicKey;

/**
 * Response message used to reply to the users after an public key request
 * with the public key associated with the device the request come from.
 */
public class PublicKeyResponse extends AuthenticationResponse {

    private static final long serialVersionUID = -8187045248923272954L;

    private byte[] publicKey;

    public PublicKeyResponse () {
    }

    public PublicKeyResponse ( PublicKey publicKey ) {
        this.publicKey = publicKey.getEncoded();
    }

    public byte[] getPublicKey () {
        return publicKey;
    }

    public void setPublicKey ( byte[] publicKey ) {
        this.publicKey = publicKey;
    }
}
