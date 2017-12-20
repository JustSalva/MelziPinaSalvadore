package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;


import java.time.Instant;

/**
 * This is the message class sent in the body of an HTTP request to add an
 * event into the user's profile
 */
public class AddEventMessage extends AddGenericEventMessage {

    private static final long serialVersionUID = 2301576598086898635L;

    private String description;
    private boolean prevLocChoice;
    private boolean travelAtLastChoice;
    private long idTypeOfEvent;
    private LocationMessage eventLocation;
    private LocationMessage departure;

    public AddEventMessage () {
    }

    public AddEventMessage ( String name, Instant startingTime, Instant endingTime,
                             PeriodMessage periodicity, String description, boolean prevLocChoice,
                             boolean travelAtLastChoice, long idTypeOfEvent,
                             LocationMessage eventLocation, LocationMessage departure ) {

        super( name, startingTime, endingTime, periodicity );
        this.description = description;
        this.prevLocChoice = prevLocChoice;
        this.travelAtLastChoice = travelAtLastChoice;
        this.idTypeOfEvent = idTypeOfEvent;
        this.eventLocation = eventLocation;
        this.departure = departure;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription ( String description ) {
        this.description = description;
    }

    public boolean isPrevLocChoice () {
        return prevLocChoice;
    }

    public void setPrevLocChoice ( boolean prevLocChoice ) {
        this.prevLocChoice = prevLocChoice;
    }

    public long getIdTypeOfEvent () {
        return idTypeOfEvent;
    }

    public void setIdTypeOfEvent ( long idTypeOfEvent ) {
        this.idTypeOfEvent = idTypeOfEvent;
    }

    public LocationMessage getEventLocation () {
        return eventLocation;
    }

    public void setEventLocation ( LocationMessage eventLocation ) {
        this.eventLocation = eventLocation;
    }

    public LocationMessage getDeparture () {
        return departure;
    }

    public void setDeparture ( LocationMessage departure ) {
        this.departure = departure;
    }

    public boolean isTravelAtLastChoice () {
        return travelAtLastChoice;
    }

    public void setTravelAtLastChoice ( boolean travelAtLastChoice ) {
        this.travelAtLastChoice = travelAtLastChoice;
    }
}
