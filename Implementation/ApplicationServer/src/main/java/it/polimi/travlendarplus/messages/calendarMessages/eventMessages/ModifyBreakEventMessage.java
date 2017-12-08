package it.polimi.travlendarplus.messages.calendarMessages.eventMessages;

import java.time.Instant;

public class ModifyBreakEventMessage extends AddBreakEventMessage {

    private static final long serialVersionUID = -1681212732442187373L;

    private long eventId;

    public ModifyBreakEventMessage() {
    }

    public ModifyBreakEventMessage( String name, Instant startingTime, Instant endingTime, PeriodMessage periodicity, long minimumTime, long eventId ) {
        super( name, startingTime, endingTime, periodicity, minimumTime );
        this.eventId = eventId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId( long eventId ) {
        this.eventId = eventId;
    }
}
