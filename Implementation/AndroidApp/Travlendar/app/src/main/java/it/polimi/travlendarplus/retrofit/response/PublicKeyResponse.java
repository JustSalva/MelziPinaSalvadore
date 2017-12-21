package it.polimi.travlendarplus.retrofit.response;


import java.security.PublicKey;

/**
 * Public key parsed from the JSON returned by the server.
 */
public class PublicKeyResponse {

    private PublicKey publicKey;

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
