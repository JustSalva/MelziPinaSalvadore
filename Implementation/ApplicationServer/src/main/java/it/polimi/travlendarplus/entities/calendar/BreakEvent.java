package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.beans.calendarManager.EventManager;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Entity( name = "BREAK_EVENT" )
@DiscriminatorValue( "BREAK_EVENT" )
public class BreakEvent extends GenericEvent {

    private static final long serialVersionUID = -38523370993953035L;

    @Column( name = "MINIMUM_TIME" )
    private long minimumTime; // in seconds

    public BreakEvent() {

    }

    public BreakEvent( String name, Instant startingTime, Instant endingTime, boolean isScheduled,
                       Period periodicity, long minimumTime ) {
        super( name, startingTime, endingTime, isScheduled, periodicity );
        this.minimumTime = minimumTime;
    }

    //constructor for generic event with no periodicity
    public BreakEvent( String name, Instant startingTime, Instant endingTime, boolean isScheduled, long minimumTime ) {
        super( name, startingTime, endingTime, isScheduled );
        this.minimumTime = minimumTime;
    }

    public static BreakEvent load( long key ) throws EntityNotFoundException {
        return GenericEntity.load( BreakEvent.class, key );
    }

    public long getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime( long minimumTime ) {
        this.minimumTime = minimumTime;
    }

    // Events overlapping with break event are passed as param.
    // This function checks if, with these events, is possible to ensure the minimum amount of time for the break event.
    // No path is taken into account in this function.
    public boolean isMinimumEnsuredNoPathRegard( ArrayList< Event > events ) {
        if ( events.size() == 0 )
            return true;
        // Checking if there is enough time before the first event.
        if ( minimumTime <= Duration.between( getStartingTime(), events.get( 0 ).getStartingTime() ).getSeconds() )
            return true;
        // Checking if there is enough time between two events.
        for ( int i = 0; i < events.size() - 1; i++ )
            if ( minimumTime <= Duration.between( events.get( i ).getEndingTime(),
                    events.get( i + 1 ).getStartingTime() ).getSeconds() )
                return true;
        //checking if there is enough time after the last event
        return minimumTime <= Duration.between( events.get( events.size() - 1 ).getEndingTime(),
                getEndingTime() ).getSeconds();
    }

    public boolean isMinimumEnsuredWithPathRegard( ArrayList< Event > events ) {
        if ( events.size() == 0 )
            return true;
        // Checking if there is enough time between the first event and its path or before the first event.
        if ( enoughTimeBeforeFirstEvent( events.get( 0 ) ) )
            return true;
        if ( events.size() > 1 ) {
            // Checking between first event and following path.
            if ( minimumTime <= Duration.between( events.get( 0 ).getEndingTime(),
                    events.get( 1 ).getFeasiblePath().getStartingTime() ).getSeconds() )
                return true;
            // Checking if there is enough time between an event and the previous/following paths.
            for ( int i = 1; i < events.size() - 1; i++ ) {
                if ( minimumTime <= Duration.between( events.get( i ).getFeasiblePath().getEndingTime(), events.get( i ).
                        getStartingTime() ).getSeconds() || minimumTime <= Duration.between( events.get( i ).
                        getEndingTime(), events.get( i + 1 ).getFeasiblePath().getStartingTime() ).getSeconds() )
                    return true;
            }
        }
        // Checking if there is enough time between the last event and its path or after the last event.
        return enoughTimeWithLastEvent( events.get( events.size() - 1 ) );
    }

    private boolean enoughTimeBeforeFirstEvent( Event event ) {
        Travel path = event.getFeasiblePath();
        return minimumTime <= Duration.between( getStartingTime(), path.getStartingTime() ).getSeconds() ||
                minimumTime <= Math.min( getEndingTime().getEpochSecond(), event.getStartingTime().getEpochSecond() ) -
                        Math.max( path.getEndingTime().getEpochSecond(), getStartingTime().getEpochSecond() );
    }

    private boolean enoughTimeWithLastEvent( Event event ) {
        Travel path = event.getFeasiblePath();
        return minimumTime <= Math.min( getEndingTime().getEpochSecond(), event.getStartingTime().getEpochSecond() ) -
                Math.max( path.getEndingTime().getEpochSecond(), getStartingTime().getEpochSecond() ) ||
                minimumTime <= Duration.between( event.getEndingTime(), getEndingTime() ).getSeconds();
    }

    @Override
    public boolean isOverlapFreeIntoSchedule( ScheduleManager scheduleManager ) {
        return scheduleManager.isBreakOverlapFreeIntoSchedule( this, false );
    }

    @Override
    public BreakEvent nextPeriodicEvent() {
        Instant startingTime = this.getStartingTime()
                .plus( this.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS );
        Instant endingTime = this.getEndingTime()
                .plus( this.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS );
        return new BreakEvent( this.getName(), startingTime, endingTime, false,
                this.getPeriodicity(), this.minimumTime );
    }

    @Override
    public void addInUserList( User user ) {
        user.addBreak( this );
    }

    @Override
    public void addEventAndModifyFollowingEvent( EventManager eventManager ) {
        eventManager.addBreakEvent( this );
    }

    @Override
    public void removeFeasiblePath() {
    }
}
