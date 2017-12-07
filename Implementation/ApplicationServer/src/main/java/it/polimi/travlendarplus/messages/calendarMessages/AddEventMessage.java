package it.polimi.travlendarplus.messages.calendarMessages;


import java.time.Instant;

public class AddEventMessage extends AddGenericEventMessage {

    private static final long serialVersionUID = 2301576598086898635L;

    private String description;
    private boolean prevLocChoice;
    private String typeOfEvent;
    private String eventLocation;
    private String departure;

    public AddEventMessage() {
    }

    public AddEventMessage( String name, Instant startingTime, Instant endingTime,
                            PeriodMessage periodicity, String description, boolean prevLocChoice,
                            String typeOfEvent, String eventLocation, String departure ) {

        super( name, startingTime, endingTime, periodicity );
        this.description = description;
        this.prevLocChoice = prevLocChoice;
        this.typeOfEvent = typeOfEvent;
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

    public String getTypeOfEvent() {
        return typeOfEvent;
    }

    public void setTypeOfEvent( String typeOfEvent ) {
        this.typeOfEvent = typeOfEvent;
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
