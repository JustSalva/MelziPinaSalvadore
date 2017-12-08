package it.polimi.travlendarplus.messages.calendarMessages.eventMessages;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.messages.calendarMessages.CalendarResponse;

public class BreakEventDescriptionResponse extends CalendarResponse {

    private static final long serialVersionUID = 4492030287195832061L;

    private BreakEvent breakEvent;

    public BreakEventDescriptionResponse() {
    }

    public BreakEventDescriptionResponse( BreakEvent breakEvent) {
        this.breakEvent = breakEvent;
    }

    public BreakEvent getBreakEvent() {
        return breakEvent;
    }

    public void setBreakEvent( BreakEvent breakEvent ) {
        this.breakEvent = breakEvent;
    }
}
