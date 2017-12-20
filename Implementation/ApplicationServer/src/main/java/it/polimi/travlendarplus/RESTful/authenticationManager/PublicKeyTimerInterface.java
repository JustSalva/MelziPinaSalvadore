package it.polimi.travlendarplus.RESTful.authenticationManager;

import it.polimi.travlendarplus.entities.RSAEncryption;

import javax.ejb.Local;

/**
 * Exposed interface of a timer realted to a keyPair
 *
 * @see PublicKeyTimer
 */
@Local
public interface PublicKeyTimerInterface {

    /**
     * Schedule a timer relative to a RSA key pair
     *
     * @param rsaEncryption
     */
    void scheduleSingleTimer ( RSAEncryption rsaEncryption );
}
