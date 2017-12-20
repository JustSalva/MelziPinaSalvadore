package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import java.time.Instant;

/**
 * This is the message class sent in the body of an HTTP request to modify a
 * breakEvent into the user's profile
 */
public class ModifyBreakEventMessage extends AddBreakEventMessage {

    private static final long serialVersionUID = -1681212732442187373L;

    private long eventId;
    private boolean propagateToPeriodicEvents;

    public ModifyBreakEventMessage () {
    }

    public ModifyBreakEventMessage ( String name, Instant startingTime, Instant endingTime, PeriodMessage periodicity,
                                     long minimumTime, long eventId, boolean propagateToPeriodicEvents ) {
        super( name, startingTime, endingTime, periodicity, minimumTime );
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
