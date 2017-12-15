package it.polimi.travlendarplus.entities.calendar;


import it.polimi.travlendarplus.beans.calendar_manager.EventManager;
import it.polimi.travlendarplus.beans.calendar_manager.ScheduleManager;
import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;

@Entity( name = "GENERIC_EVENT" )
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn( name = "EVENT_TYPE" )
public abstract class GenericEvent extends EntityWithLongKey implements Comparable< GenericEvent > {

    private static final long serialVersionUID = -4348542805788613273L;

    private final long SECONDS_IN_A_DAY = 24 * 60 * 60;

    @Column( nullable = false, name = "NAME" )
    private String name;

    @Column( name = "STARTING_TIME" )
    private Instant startingTime;

    @Column( name = "ENDING_TIME" )
    private Instant endingTime;

    @Column( name = "IS_SCHEDULED" )
    private boolean isScheduled;

    @OneToOne( cascade = CascadeType.ALL )
    @JoinColumn( name = "PERIODICITY_ID" )
    private Period periodicity;

    private String userId;

    @Embedded
    private Timestamp lastUpdate;

    public GenericEvent() {
    }

    public GenericEvent( String name, Instant startingTime, Instant endingTime, boolean isScheduled,
                         Period periodicity ) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.isScheduled = isScheduled;
        this.periodicity = periodicity;
        this.lastUpdate = new Timestamp();
    }

    //constructor for generic event with no periodicity
    public GenericEvent( String name, Instant startingTime, Instant endingTime, boolean isScheduled ) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.isScheduled = isScheduled;
        this.periodicity = new Period( null, null, 0 );
        this.lastUpdate = new Timestamp();
    }

    @Override
    public int compareTo( GenericEvent gEvent ) {
        return startingTime.isAfter( gEvent.startingTime ) ? 1 :
                startingTime.isBefore( gEvent.startingTime ) ? -1 : 0;
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

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled( boolean scheduled ) {
        isScheduled = scheduled;
    }

    public Period getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity( Period periodicity ) {
        this.periodicity = periodicity;
    }

    public long getDayAtMidnight() {
        return startingTime.getEpochSecond() - ( startingTime.getEpochSecond() % ( SECONDS_IN_A_DAY ) );
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate( Timestamp lastUpdate ) {
        this.lastUpdate = lastUpdate;
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId( String userId ) {
        this.userId = userId;
    }

    public User getUser() {
        try {
            return User.load( this.userId );
        } catch ( EntityNotFoundException e ) {
            //it always have to be found
            return null;
        }
    }

    public void setUser( User user ) {
        this.userId = user.getEmail();
    }

    //used to remove correctly an event into function of ScheduleHolder class
    @Override
    public boolean equals( Object event ) {
        return getId() == ( ( GenericEvent ) event ).getId();
    }

    public abstract GenericEvent nextPeriodicEvent();

    public abstract boolean isOverlapFreeIntoSchedule( ScheduleManager scheduleManager );

    public abstract void addInUserList( User user );

    public abstract void addEventAndModifyFollowingEvent( EventManager eventManager );

    public abstract void removeFeasiblePath();

    @Override
    public String toString() {
        return "GenericEvent{" +
                "name='" + name + '\'' +
                ", startingTime=" + startingTime +
                ", endingTime=" + endingTime +
                ", isScheduled=" + isScheduled +
                ", periodicity=" + periodicity +
                ", userId=" + userId +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
