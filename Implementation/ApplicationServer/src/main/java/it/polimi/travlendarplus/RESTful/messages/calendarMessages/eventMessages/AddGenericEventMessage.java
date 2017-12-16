package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.CalendarMessage;

import java.time.Instant;

public abstract class AddGenericEventMessage extends CalendarMessage {

    private static final long serialVersionUID = -1177938740712276321L;

    private String name;
    private Instant startingTime;
    private Instant endingTime;
    private PeriodMessage periodicity;

    public AddGenericEventMessage() {
    }

    public AddGenericEventMessage( String name, Instant startingTime, Instant endingTime, PeriodMessage periodicity ) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.periodicity = periodicity;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Instant getStartingTime() {
        return startingTime;
    }

    public void setStartingTime( Instant startingTime ) {
        this.startingTime = startingTime;
    }

    public Instant getEndingTime() {
        return endingTime;
    }

    public void setEndingTime( Instant endingTime ) {
        this.endingTime = endingTime;
    }

    public PeriodMessage getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity( PeriodMessage periodicity ) {
        this.periodicity = periodicity;
    }
}
