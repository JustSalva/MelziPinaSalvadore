package it.polimi.travlendarplus.entities.calendar;


import it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages.EventsListResponse;
import it.polimi.travlendarplus.beans.calendarManager.EventManager;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;

@Entity( name = "GENERIC_EVENT" )
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn( name = "EVENT_TYPE" )
public abstract class GenericEvent extends EntityWithLongKey implements Comparable < GenericEvent > {

    private static final long serialVersionUID = -4348542805788613273L;

    @Column( nullable = false, name = "NAME" )
    private String name;

    @Column( name = "STARTING_TIME" )
    private Instant startingTime;

    @Column( name = "ENDING_TIME" )
    private Instant endingTime;

    @Column( name = "IS_SCHEDULED" )
    private boolean isScheduled;

    private long periodicityId;

    private String userId;

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

    public abstract GenericEvent nextPeriodicEvent ();

    public abstract boolean isOverlapFreeIntoSchedule ( ScheduleManager scheduleManager );

    public abstract void addInUserList ( User user );

    public abstract void addEventAndModifyFollowingEvent ( EventManager eventManager );

    public abstract void removeFeasiblePath ();

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
