package it.polimi.travlendarplus.beans.emailManager;

import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.MailPasswordForwardingFailedException;

import javax.ejb.Local;

/**
 *  Interface to be injected in order to sends emails
 */
@Local
public interface EmailInterface {
    /**
     * Allows the user to receive his credentials through his email
     *
     * @param user Travlendar plus user who wants to reset his password
     * @throws MailPasswordForwardingFailedException if an error occurs in email forwarding
     */
    void sendNewCredentials ( User user ) throws MailPasswordForwardingFailedException;
}
