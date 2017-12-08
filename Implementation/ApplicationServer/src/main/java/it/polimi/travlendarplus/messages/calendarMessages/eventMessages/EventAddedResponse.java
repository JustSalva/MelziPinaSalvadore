package it.polimi.travlendarplus.messages.calendarMessages.eventMessages;

import it.polimi.travlendarplus.messages.calendarMessages.CalendarResponse;

public class EventAddedResponse extends CalendarResponse {

    private static final long serialVersionUID = -6278439694207006745L;

    private long eventId;

    public EventAddedResponse() {
    }

    public EventAddedResponse( long eventId ) {
        this.eventId = eventId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId( long eventId ) {
        this.eventId = eventId;
    }
}
