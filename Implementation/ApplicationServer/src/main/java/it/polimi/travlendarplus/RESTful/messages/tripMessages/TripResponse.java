package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.RESTful.messages.GenericResponseMessage;

/**
 * This is the main response message class used to reply to all related to trip functionalities requests;
 * It has to be extended by all messages used to reply to such user requests
 */
public abstract class TripResponse extends GenericResponseMessage {

    private static final long serialVersionUID = -444973042645027772L;
}
