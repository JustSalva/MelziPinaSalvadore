package it.polimi.travlendarplus.retrofit.response;


/**
 * Public key parsed from the JSON returned by the server.
 */
public class PublicKeyResponse {

    private byte[] publicKey;

    public byte[] getPublicKey () {
        return publicKey;
    }
}
