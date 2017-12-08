package it.polimi.travlendarplus.messages.calendarMessages.eventMessages;


import java.time.Instant;

public class AddEventMessage extends AddGenericEventMessage {

    private static final long serialVersionUID = 2301576598086898635L;

    private String description;
    private boolean prevLocChoice;
    private long idTypeOfEvent;
    private String eventLocation;
    private String departure;

    public AddEventMessage() {
    }

    public AddEventMessage( String name, Instant startingTime, Instant endingTime,
                            PeriodMessage periodicity, String description, boolean prevLocChoice,
                            long idTypeOfEvent, String eventLocation, String departure ) {

        super( name, startingTime, endingTime, periodicity );
        this.description = description;
        this.prevLocChoice = prevLocChoice;
        this.idTypeOfEvent = idTypeOfEvent;
        this.eventLocation = eventLocation;
        this.departure = departure;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public boolean isPrevLocChoice() {
        return prevLocChoice;
    }

    public void setPrevLocChoice( boolean prevLocChoice ) {
        this.prevLocChoice = prevLocChoice;
    }

    public long getIdTypeOfEvent() {
        return idTypeOfEvent;
    }

    public void setIdTypeOfEvent( long idTypeOfEvent ) {
        this.idTypeOfEvent = idTypeOfEvent;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation( String eventLocation ) {
        this.eventLocation = eventLocation;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture( String departure ) {
        this.departure = departure;
    }
}
