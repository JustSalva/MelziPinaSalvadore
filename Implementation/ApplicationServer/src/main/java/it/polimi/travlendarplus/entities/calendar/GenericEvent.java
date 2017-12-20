package it.polimi.travlendarplus.entities.calendar;


import it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages.EventsListResponse;
import it.polimi.travlendarplus.beans.calendarManager.EventManager;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;

/**
 * This JPA class represent a generic event structure, it is to be implemented by both event and break event classes
 */
@Entity( name = "GENERIC_EVENT" )
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn( name = "EVENT_TYPE" )
public abstract class GenericEvent extends EntityWithLongKey implements Comparable < GenericEvent > {

    private static final long serialVersionUID = -4348542805788613273L;

    /**
     * Name given to the event by the user
     */
    @Column( nullable = false, name = "NAME" )
    private String name;

    /**
     * Unix time at which the event starts
     */
    @Column( name = "STARTING_TIME" )
    private Instant startingTime;

    /**
     * Unix time at which the event ends
     */
    @Column( name = "ENDING_TIME" )
    private Instant endingTime;

    /**
     * Flag that state if an event is scheduled or not
     */
    @Column( name = "IS_SCHEDULED" )
    private boolean isScheduled;

    /**
     * Identifier of the tuple in period table tat represent the event's periodicity,
     * it is equal to zero if the event doesn'y have a periodicity
     */
    private long periodicityId;

    /**
     * Identifier of the user that owns the event
     */
    private String userId;

    /**
     * Timestamp in Unix time that memorize the last update of an event
     */
    @Embedded
    private Timestamp lastUpdate;

    public GenericEvent () {
    }

    public GenericEvent ( String name, Instant startingTime, Instant endingTime, boolean isScheduled,
                          Period periodicity ) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.isScheduled = isScheduled;
        this.periodicityId = periodicity.getId();
        this.lastUpdate = new Timestamp();
    }

    //constructor for generic event with no periodicity
    public GenericEvent ( String name, Instant startingTime, Instant endingTime, boolean isScheduled ) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.isScheduled = isScheduled;
        this.periodicityId = 0;
        this.lastUpdate = new Timestamp();
    }

    @Override
    public int compareTo ( GenericEvent gEvent ) {
        if ( startingTime == null || gEvent.startingTime == null )
            return -1;
        return startingTime.isAfter( gEvent.startingTime ) ? 1 :
                startingTime.isBefore( gEvent.startingTime ) ? -1 : 0;
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public Instant getStartingTime () {
        return startingTime;
    }

    public void setStartingTime ( Instant startingTime ) {
        this.startingTime = startingTime;
    }

    public Instant getEndingTime () {
        return endingTime;
    }

    public void setEndingTime ( Instant endingTime ) {
        this.endingTime = endingTime;
    }

    public boolean isScheduled () {
        return isScheduled;
    }

    public void setScheduled ( boolean scheduled ) {
        isScheduled = scheduled;
    }

    /**
     * Loads the period class relative to the event
     *
     * @return the period class if present, null otherwise
     */
    public Period getPeriodicity () {
        try {
            return Period.load( periodicityId );
        } catch ( EntityNotFoundException e ) {
            //if is not found the id is equal to zero => no periodicity
            return null;
        }
    }

    public void setPeriodicity ( Period periodicity ) {
        this.periodicityId = periodicity.getId();
    }

    public Timestamp getLastUpdate () {
        return lastUpdate;
    }

    public void setLastUpdate ( Timestamp lastUpdate ) {
        this.lastUpdate = lastUpdate;
    }

    public String getuserId () {
        return userId;
    }

    public void setuserId ( String userId ) {
        this.userId = userId;
    }

    /**
     * Loads the user class that owns the event
     *
     * @return the user class if present, null otherwise
     */
    public User getUser () {
        try {
            return User.load( this.userId );
        } catch ( EntityNotFoundException e ) {
            //it always have to be found
            return null;
        }
    }

    public void setUser ( User user ) {
        this.userId = user.getEmail();
    }

    //used to remove correctly an event into function of ScheduleHolder class
    @Override
    public boolean equals ( Object event ) {
        return getId() == ( ( GenericEvent ) event ).getId();
    }

    /**
     * Provide the instance of the next periodic event to be propagated in time
     *
     * @return teh instance of the next periodic event
     */
    public abstract GenericEvent nextPeriodicEvent ();


    /**
     * Visitor method used during the scheduling computation process of an event, it is used to check
     * if the event overlaps with other events
     *
     * @param scheduleManager manager which is handling the scheduling process of the event
     * @return true if the event does not overlap with other events, false otherwise
     */
    public abstract boolean isOverlapFreeIntoSchedule ( ScheduleManager scheduleManager );

    /**
     * Visitor method used to add an event in the right list of the user class
     *
     * @param user owner of the event
     */
    public abstract void addInUserList ( User user );

    /**
     * Visitor method used in order to add an event into the schedule and modify the path
     * that will leads to teh following event
     *
     * @param eventManager manager which is handling the event
     * @throws GMapsGeneralException if the path computation fails cause Google maps services are unavailable
     */
    public abstract void addEventAndModifyFollowingEvent ( EventManager eventManager )
            throws GMapsGeneralException;

    /**
     * Removes the feasible path of an event, used when the event is put into the non scheduled list
     */
    public abstract void removeFeasiblePath ();

    /**
     * Visitor method used to serialize a correct response to the client,
     * useful in order to handle all events in the same way
     *
     * @param eventsListResponse message that is to be sent to the client
     */
    public abstract void serializeResponse ( EventsListResponse eventsListResponse );

    @Override
    public String toString () {
        return "GenericEvent{" +
                "name='" + name + '\'' +
                ", startingTime=" + startingTime +
                ", endingTime=" + endingTime +
                ", isScheduled=" + isScheduled +
                ", periodicityId=" + periodicityId +
                ", userId=" + userId +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
