package it.polimi.travlendarplus.RESTful.security;

import it.polimi.travlendarplus.entities.RSAEncryption;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;

@Stateless
public class PublicKeyTimer implements PublicKeyTimerInterface
{
    private static final long TIMER_MILLISECONDS_DURATION = 60*1000;

    private @Resource SessionContext ctx;

    public void scheduleSingleTimer( RSAEncryption rsaEncryption )
    {
        ctx.getTimerService().createTimer( TIMER_MILLISECONDS_DURATION, rsaEncryption.getIdDevice() );
    }

    @Timeout
    public void timeoutHandler( Timer timer )
    {
        RSAEncryption rsaEncryption ;
        String idDevice= (String) timer.getInfo();
        try {
            rsaEncryption = RSAEncryption.load( idDevice );
            rsaEncryption.remove();
        } catch ( EntityNotFoundException e ) {
            //Do nothing the row has already been deleted
        }

        timer.cancel(); //It should be useless
    }
}

