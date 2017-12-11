package it.polimi.travlendarplus.messages.calendarMessages.eventMessages;

import java.time.Instant;

public class ModifyEventMessage extends AddEventMessage {

    private static final long serialVersionUID = -8324907783009426089L;

    private long eventId;
    private boolean propagateToPeriodicEvents;

    public ModifyEventMessage( ) {
    }

    public ModifyEventMessage( String name, Instant startingTime, Instant endingTime, PeriodMessage periodicity,
                               String description, boolean prevLocChoice, long idTypeOfEvent, String eventLocation,
                               String departure, long eventId, boolean propagateToPeriodicEvents) {

        super( name, startingTime, endingTime, periodicity, description, prevLocChoice,
                idTypeOfEvent, eventLocation, departure );
        this.eventId = eventId;
        this.propagateToPeriodicEvents = propagateToPeriodicEvents;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId( long eventId ) {
        this.eventId = eventId;
    }

    public boolean isPropagateToPeriodicEvents() {
        return propagateToPeriodicEvents;
    }

    public void setPropagateToPeriodicEvents( boolean propagateToPeriodicEvents ) {
        this.propagateToPeriodicEvents = propagateToPeriodicEvents;
    }
}
