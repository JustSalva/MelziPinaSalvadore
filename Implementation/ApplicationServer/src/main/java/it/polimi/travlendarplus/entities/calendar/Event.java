package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.beans.calendarManager.EventManager;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity( name = "EVENT" )
@DiscriminatorValue( "EVENT" )
public class Event extends GenericEvent {

    private static final long serialVersionUID = 8421808089635462963L;

    @Column( name = "DESCRIPTION" )
    private String description;

    @Column( name = "PREV_LOCATION_CHOICE" )
    private boolean prevLocChoice;

    @Column( name = "TRAVEL_AT_LAST_CHOICE" )
    private boolean travelAtLastChoice;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "TYPE_OF_EVENT" )
    private TypeOfEvent type;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumns( {
            @JoinColumn( name = "EVENT_LATITUDE", referencedColumnName = "latitude" ),
            @JoinColumn( name = "EVENT_LONGITUDE", referencedColumnName = "longitude" )
    } )
    private Location eventLocation;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumns( {
            @JoinColumn( name = "DEPARTURE_LATITUDE", referencedColumnName = "latitude" ),
            @JoinColumn( name = "DEPARTURE_LONGITUDE", referencedColumnName = "longitude" )
    } )
    private Location departure;

    @OneToOne( fetch = FetchType.LAZY, cascade = CascadeType.ALL )
    @JoinColumn( name = "FEASIBLE_TRAVEL" )
    private Travel feasiblePath;

    public Event () {
    }

    public Event ( String name, Instant startingTime, Instant endingTime, boolean isScheduled, Period periodicity,
                   String description, boolean prevLocChoice, boolean travelAtLastChoice, TypeOfEvent type,
                   Location eventLocation, Location departure ) {
        super( name, startingTime, endingTime, isScheduled, periodicity );
        this.description = description;
        this.prevLocChoice = prevLocChoice;
        this.travelAtLastChoice = travelAtLastChoice;
        this.type = type;
        this.eventLocation = eventLocation;
        this.departure = departure;
    }

    //constructor for generic event with no periodicity
    public Event ( String name, Instant startingTime, Instant endingTime, boolean isScheduled,
                   String description, boolean prevLocChoice, boolean travelAtLastChoice, TypeOfEvent type, Location eventLocation,
                   Location departure, Travel feasiblePath ) {
        super( name, startingTime, endingTime, isScheduled );
        this.description = description;
        this.prevLocChoice = prevLocChoice;
        this.travelAtLastChoice = travelAtLastChoice;
        this.type = type;
        this.eventLocation = eventLocation;
        this.departure = departure;
        this.feasiblePath = feasiblePath;
    }

    public static Event load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( Event.class, key );
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

    public TypeOfEvent getType () {
        return type;
    }

    public void setType ( TypeOfEvent type ) {
        this.type = type;
    }

    public Location getEventLocation () {
        return eventLocation;
    }

    public void setEventLocation ( Location eventLocation ) {
        this.eventLocation = eventLocation;
    }

    public Location getDeparture () {
        return departure;
    }

    public void setDeparture ( Location departure ) {
        this.departure = departure;
    }

    public Travel getFeasiblePath () {
        return feasiblePath;
    }

    public void setFeasiblePath ( Travel feasiblePath ) {
        this.feasiblePath = feasiblePath;
    }

    public boolean isTravelAtLastChoice () {
        return travelAtLastChoice;
    }

    public void setTravelAtLastChoice ( boolean travelAtLastChoice ) {
        this.travelAtLastChoice = travelAtLastChoice;
    }

    public Event nextPeriodicEvent () {
        Instant startingTime = this.getStartingTime().plus( this.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS );
        Instant endingTime = this.getEndingTime().plus( this.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS );
        return new Event( this.getName(), startingTime, endingTime, false, this.getPeriodicity(),
                this.description, this.prevLocChoice, this.travelAtLastChoice,
                this.type, this.eventLocation, this.departure );
    }

    @Override
    public boolean isOverlapFreeIntoSchedule ( ScheduleManager scheduleManager ) {
        return scheduleManager.isEventOverlapFreeIntoSchedule( this, false );
    }

    @Override
    public void addInUserList ( User user ) {
        user.addEvent( this );
    }

    @Override
    public void addEventAndModifyFollowingEvent ( EventManager eventManager ) {
        eventManager.addEventAndModifyFollowingEvent( this );
    }

    @Override
    public void removeFeasiblePath () {
        feasiblePath = null;
    }

    @Override
    public String toString () {
        return "Event{" + super.toString() +
                "description='" + description + '\'' +
                ", prevLocChoice=" + prevLocChoice +
                ", type=" + type +
                ", eventLocation=" + eventLocation +
                ", departure=" + departure +
                ", feasiblePath=" + feasiblePath +
                '}';
    }
}
