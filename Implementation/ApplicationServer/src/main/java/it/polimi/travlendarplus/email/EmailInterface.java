package it.polimi.travlendarplus.email;

import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.MailPasswordForwardingFailedException;

import javax.ejb.Local;

@Local
public interface EmailInterface {
    void sendNewCredentials ( User user ) throws MailPasswordForwardingFailedException ;
}
