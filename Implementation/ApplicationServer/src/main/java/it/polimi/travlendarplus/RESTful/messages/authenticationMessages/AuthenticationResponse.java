package it.polimi.travlendarplus.RESTful.messages.authenticationMessages;


import it.polimi.travlendarplus.RESTful.messages.GenericResponseMessage;

/**
 * Main authentication message response class, to be extended by all messages used
 * to reply to the user after an authentication request.
 */
public abstract class AuthenticationResponse extends GenericResponseMessage {

    private static final long serialVersionUID = 1013533006810812161L;
}
