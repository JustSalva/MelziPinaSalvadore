package it.polimi.travlendarplus.RESTful.security;

import it.polimi.travlendarplus.entities.RSAEncryption;

import javax.ejb.Local;

@Local
public interface PublicKeyTimerInterface {
    void scheduleSingleTimer( RSAEncryption rsaEncryption);
}
