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
     * @param rsaEncryption container of the pair of keys to be deleted by timer
     */
    void scheduleSingleTimer ( RSAEncryption rsaEncryption );
}
