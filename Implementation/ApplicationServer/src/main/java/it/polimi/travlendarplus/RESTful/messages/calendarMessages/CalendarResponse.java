package it.polimi.travlendarplus.RESTful.messages.calendarMessages;

import it.polimi.travlendarplus.RESTful.messages.GenericResponseMessage;

/**
 * This is the main response message class used to reply to all related to calendar functionalities requests;
 * It has to be extended by all messages used to reply to such user requests
 */
public abstract class CalendarResponse extends GenericResponseMessage {

    private static final long serialVersionUID = -1113545392291139781L;

}
