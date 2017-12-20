package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.CalendarMessage;

import java.time.Instant;

/**
 * Message used as a container of a periodicity info.
 * This class can be embedded into others message classes in order to
 * contain such info
 */
public class PeriodMessage extends CalendarMessage {

    private static final long serialVersionUID = 3808928000453037348L;

    private Instant startingDay;
    private Instant endingDay;
    private int deltaDays;

    public PeriodMessage () {
    }

    public PeriodMessage ( Instant startingDay, Instant endingDay, int deltaDays ) {
        this.startingDay = startingDay;
        this.endingDay = endingDay;
        this.deltaDays = deltaDays;
    }

    public Instant getStartingDay () {
        return startingDay;
    }

    public void setStartingDay ( Instant startingDay ) {
        this.startingDay = startingDay;
    }

    public Instant getEndingDay () {
        return endingDay;
    }

    public void setEndingDay ( Instant endingDay ) {
        this.endingDay = endingDay;
    }

    public int getDeltaDays () {
        return deltaDays;
    }

    public void setDeltaDays ( int deltaDays ) {
        this.deltaDays = deltaDays;
    }
}
