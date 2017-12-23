package it.polimi.travlendarplus.RESTful.authenticationManager;

import it.polimi.travlendarplus.entities.RSAEncryption;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;

/**
 * This stateless EJB provide a timer that is to be scheduled right after a key
 * pair is created. When the timer will exceed the key pair (NB. public and private )
 * will be considered exceeded and so removed from the database
 */
@Stateless
public class PublicKeyTimer implements PublicKeyTimerInterface {

    private static final long TIMER_MILLISECONDS_DURATION = 10 * 60 * 1000;

    /**
     * Context in which the timer is to be scheduled
     */
    private @Resource
    SessionContext ctx;

    /**
     * Schedule a timer relative to a RSA key pair
     *
     * @param rsaEncryption
     */
    public void scheduleSingleTimer ( RSAEncryption rsaEncryption ) {
        ctx.getTimerService().createTimer( TIMER_MILLISECONDS_DURATION, rsaEncryption.getIdDevice() );
    }

    /**
     * When the timer exceed it removes from the database the key pair class
     *
     * @param timer timer that is exceeded
     */
    @Timeout
    public void timeoutHandler ( Timer timer ) {
        RSAEncryption rsaEncryption;
        String idDevice = ( String ) timer.getInfo();
        try {
            rsaEncryption = RSAEncryption.load( idDevice );
            rsaEncryption.remove();
        } catch ( EntityNotFoundException e ) {
            //Do nothing cause the row has already been deleted
        }
        timer.cancel(); //It should be useless
    }
}

