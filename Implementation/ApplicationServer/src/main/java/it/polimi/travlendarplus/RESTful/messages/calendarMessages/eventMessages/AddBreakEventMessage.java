package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import java.time.Instant;

public class AddBreakEventMessage extends AddGenericEventMessage {

    private static final long serialVersionUID = -3068119858754945460L;

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
