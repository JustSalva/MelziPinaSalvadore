package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.CalendarResponse;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a message class sent to reply with all needed info
 * to a request that expects a list of event and breakEvents as reply
 */
public class EventsListResponse extends CalendarResponse {

    private static final long serialVersionUID = 8768153863882742282L;

    private List < Event > updatedEvents;
    private List < BreakEvent > updatedBreakEvents;

    public EventsListResponse ( List < GenericEvent > updatedEvents ) {
        this.updatedEvents = new ArrayList <>();
        this.updatedBreakEvents = new ArrayList <>();
        for ( GenericEvent genericEvent : updatedEvents ){
            genericEvent.serializeResponse( this );
        }
    }

    public List < Event > getUpdatedEvents () {
        return updatedEvents;
    }

    public void setUpdatedEvents ( List < Event > updatedEvents ) {
        this.updatedEvents = updatedEvents;
    }

    public void addUpdatedEvent ( Event updatedEvent ) {
        this.updatedEvents.add( updatedEvent );
    }

    public List < BreakEvent > getUpdatedBreakEvents () {
        return updatedBreakEvents;
    }

    public void setUpdatedBreakEvents ( List < BreakEvent > updatedBreakEvents ) {
        this.updatedBreakEvents = updatedBreakEvents;
    }

    public void addUpdatedBreakEvents ( BreakEvent updatedBreakEvent ) {
        this.updatedBreakEvents.add( updatedBreakEvent );
    }

}
