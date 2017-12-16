package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.CalendarResponse;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;

import java.util.List;

public class EventsListResponse extends CalendarResponse {

    private static final long serialVersionUID = 8768153863882742282L;

    private List< GenericEvent > updatedEvents;

    public EventsListResponse( List< GenericEvent > updatedEvents ) {
        this.updatedEvents = updatedEvents;
    }

    public List< GenericEvent > getUpdatedEvents() {
        return updatedEvents;
    }

    public void setUpdatedEvents( List< GenericEvent > updatedEvents ) {
        this.updatedEvents = updatedEvents;
    }
}
