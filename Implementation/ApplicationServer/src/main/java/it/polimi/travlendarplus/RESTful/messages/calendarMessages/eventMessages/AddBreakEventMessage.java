package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import java.time.Instant;

/**
 * This is the message class sent in the body of an HTTP request to add a
 * break event into the user's profile
 */
public class AddBreakEventMessage extends AddGenericEventMessage {

    private static final long serialVersionUID = -3068119858754945460L;

    /**
     * Minimum time that is to be respected to schedule a break event
     */
    private long minimumTime; // in seconds

    public AddBreakEventMessage () {
    }

    public AddBreakEventMessage ( String name, Instant startingTime, Instant endingTime, PeriodMessage periodicity, long minimumTime ) {
        super( name, startingTime, endingTime, periodicity );
        this.minimumTime = minimumTime;
    }

    public long getMinimumTime () {
        return minimumTime;
    }

    public void setMinimumTime ( long minimumTime ) {
        this.minimumTime = minimumTime;
    }
}
