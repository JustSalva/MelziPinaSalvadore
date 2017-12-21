package it.polimi.travlendarplus.activity;


import java.security.PublicKey;

/**
 * Interface to be implemented by the activities that need to receive a public key from the server.
 */
public interface PublicKeyActivity {
    void setPublicKey(PublicKey publicKey);
    void resumeNormalMode();
}
