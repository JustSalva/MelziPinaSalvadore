package it.polimi.travlendarplus.messages.calendarMessages.eventMessages;

import java.time.Instant;

public class ModifyEventMessage extends AddEventMessage {

    private static final long serialVersionUID = -8324907783009426089L;

    private long eventId;

    public ModifyEventMessage( long eventId ) {
        this.eventId = eventId;
    }

    public ModifyEventMessage( String name, Instant startingTime, Instant endingTime, PeriodMessage periodicity,
                               String description, boolean prevLocChoice, long idTypeOfEvent, String eventLocation,
                               String departure, long eventId ) {

        super( name, startingTime, endingTime, periodicity, description, prevLocChoice,
                idTypeOfEvent, eventLocation, departure );
        this.eventId = eventId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId( long eventId ) {
        this.eventId = eventId;
    }
}
