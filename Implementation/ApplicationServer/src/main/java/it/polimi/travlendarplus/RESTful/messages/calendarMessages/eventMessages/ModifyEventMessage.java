package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import java.time.Instant;

/**
 * This is the message class sent in the body of an HTTP request to modify an
 * event into the user's profile
 */
public class ModifyEventMessage extends AddEventMessage {

    private static final long serialVersionUID = -8324907783009426089L;

    private long eventId;
    private boolean propagateToPeriodicEvents;

    public ModifyEventMessage () {
    }

    public ModifyEventMessage ( String name, Instant startingTime, Instant endingTime, PeriodMessage periodicity,
                                String description, boolean prevLocChoice, boolean travelAtLastChoice,
                                long idTypeOfEvent, LocationMessage eventLocation, LocationMessage departure,
                                long eventId, boolean propagateToPeriodicEvents ) {

        super( name, startingTime, endingTime, periodicity, description, prevLocChoice, travelAtLastChoice,
                idTypeOfEvent, eventLocation, departure );
        this.eventId = eventId;
        this.propagateToPeriodicEvents = propagateToPeriodicEvents;
    }

    public long getEventId () {
        return eventId;
    }

    public void setEventId ( long eventId ) {
        this.eventId = eventId;
    }

    public boolean isPropagateToPeriodicEvents () {
        return propagateToPeriodicEvents;
    }

    public void setPropagateToPeriodicEvents ( boolean propagateToPeriodicEvents ) {
        this.propagateToPeriodicEvents = propagateToPeriodicEvents;
    }
}
