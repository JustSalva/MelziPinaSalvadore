package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.CalendarResponse;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;

/**
 * This is a message class sent to reply with all needed info
 * to a breakEvent - related response
 */
public class BreakEventDescriptionResponse extends CalendarResponse {

    private static final long serialVersionUID = 4492030287195832061L;

    private BreakEvent breakEvent;

    public BreakEventDescriptionResponse () {
    }

    public BreakEventDescriptionResponse ( BreakEvent breakEvent ) {
        this.breakEvent = breakEvent;
    }

    public BreakEvent getBreakEvent () {
        return breakEvent;
    }

    public void setBreakEvent ( BreakEvent breakEvent ) {
        this.breakEvent = breakEvent;
    }
}
