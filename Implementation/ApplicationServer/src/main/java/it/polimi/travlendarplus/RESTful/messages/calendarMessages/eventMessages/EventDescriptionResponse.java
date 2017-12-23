package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.CalendarResponse;
import it.polimi.travlendarplus.entities.calendar.Event;

/**
 * This is a message class sent to reply with all needed info
 * to a event - related response
 */
public class EventDescriptionResponse extends CalendarResponse {

    private static final long serialVersionUID = 10526724171365942L;

    private Event event;

    public EventDescriptionResponse () {
    }

    public EventDescriptionResponse ( Event event ) {
        this.event = event;
    }

    public Event getEvent () {
        return event;
    }

    public void setEvent ( Event event ) {
        this.event = event;
    }
}
