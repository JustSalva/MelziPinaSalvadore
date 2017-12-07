package it.polimi.travlendarplus.messages.calendarMessages;

import it.polimi.travlendarplus.messages.GenericMessage;

/**
 * This is the main message class used to perform calendar functionalities requests;
 * It has to be extended by all messages used to request to such functionalities.
 */
public abstract class CalendarMessage extends GenericMessage {

    private static final long serialVersionUID = -4284302445297855152L;
}
