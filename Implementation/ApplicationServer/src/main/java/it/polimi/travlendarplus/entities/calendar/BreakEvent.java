package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages.EventsListResponse;
import it.polimi.travlendarplus.beans.calendarManager.EventManager;
import it.polimi.travlendarplus.beans.calendarManager.PathManager;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * This JPA class represent a break event
 */
@Entity( name = "BREAK_EVENT" )
@DiscriminatorValue( "BREAK_EVENT" )
public class BreakEvent extends GenericEvent {

    private static final long serialVersionUID = -38523370993953035L;

    /**
     * Minimum amount of "free time" that has to be satisfied in order to
     * guarantee that the break event is scheduled
     */
    @Column( name = "MINIMUM_TIME" )
    private long minimumTime; // in seconds

    public BreakEvent () {
    }

    public BreakEvent ( String name, Instant startingTime, Instant endingTime,
                        boolean isScheduled, Period periodicity, long minimumTime ) {
        super( name, startingTime, endingTime, isScheduled, periodicity );
        this.minimumTime = minimumTime;
    }

    //constructor for generic event with no periodicity
    public BreakEvent ( String name, Instant startingTime, Instant endingTime,
                        boolean isScheduled, long minimumTime ) {
        super( name, startingTime, endingTime, isScheduled );
        this.minimumTime = minimumTime;
    }

    /**
     * Allows to load a BreakEvent class from the database
     *
     * @param key primary key of the breakEvent tuple
     * @return the requested tuple as a BreakEvent class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static BreakEvent load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( BreakEvent.class, key );
    }

    public long getMinimumTime () {
        return minimumTime;
    }

    public void setMinimumTime ( long minimumTime ) {
        this.minimumTime = minimumTime;
    }

    /**
     * This method checks if is possible, with the provided events, to ensure
     * the minimum amount of time for the break event.
     * No event's paths are taken into account in this method.
     *
     * @param events Events overlapping with this breakEvent instance
     * @return true if the minimum time is guaranteed, false otherwise
     */
    public boolean isMinimumEnsuredNoPathRegard ( List < Event > events ) {
        if ( events.size() == 0 )
            return true;
        // Checking if there is enough time before the first event.
        if ( minimumTime <= Duration.between( getStartingTime(),
                events.get( 0 ).getStartingTime() ).getSeconds() )
            return true;
        // Checking if there is enough time between two events.
        for ( int i = 0; i < events.size() - 1; i++ )
            if ( minimumTime <= Duration.between( events.get( i ).getEndingTime(),
                    events.get( i + 1 ).getStartingTime() ).getSeconds() )
                return true;
        //checking if there is enough time after the last event
        return minimumTime <= Duration.between(
                events.get( events.size() - 1 ).getEndingTime(),
                getEndingTime() ).getSeconds();
    }

    /**
     * This method checks if is possible, with the provided events, to ensure
     * the minimum amount of time for the break event.
     * Event's paths are taken into account in this method.
     *
     * @param events Events overlapping with this breakEvent instance
     * @return true if the minimum time is guaranteed, false otherwise
     */
    public boolean isMinimumEnsuredWithPathRegard ( List < Event > events ) {
        if ( events.size() == 0 )
            return true;
        /* Checking if there is enough time between the first event and its path or before the first event.
         */
        if ( enoughTimeBeforeFirstEvent( events.get( 0 ) ) )
            return true;
        if ( events.size() > 1 ) {
            // Checking between first event and following path.
            if ( minimumTime <= Duration.between( events.get( 0 ).getEndingTime(),
                    events.get( 1 ).getFeasiblePath().getStartingTime() ).getSeconds() )
                return true;
            // Checking if there is enough time between an event and the previous/following paths.
            for ( int i = 1; i < events.size() - 1; i++ ) {
                if ( minimumTime <= Duration.between( events.get( i ).getFeasiblePath().getEndingTime(),
                        events.get( i ).getStartingTime() ).getSeconds() ||
                        minimumTime <= Duration.between( events.get( i ).getEndingTime(),
                                events.get( i + 1 ).getFeasiblePath().getStartingTime() ).getSeconds() )
                    return true;
            }
        }
        // Checking if there is enough time between the last event and its path or after the last event.
        return enoughTimeWithLastEvent( events.get( events.size() - 1 ) );
    }

    /**
     * Checks if the minimum time is guaranteed considering only the first event ( in time )
     * that overlaps with the break event
     *
     * @param event the first event that overlaps in time
     * @return true if the minimum time is guaranteed, false otherwise
     */
    private boolean enoughTimeBeforeFirstEvent ( Event event ) {
        Travel path = event.getFeasiblePath();
        return minimumTime <= Duration.between( getStartingTime(), path.getStartingTime() ).getSeconds() ||
                minimumTime <= Math.min( getEndingTime().getEpochSecond(), event.getStartingTime().getEpochSecond() ) -
                        Math.max( path.getEndingTime().getEpochSecond(), getStartingTime().getEpochSecond() );
    }

    /**
     * Checks if the minimum time is guaranteed considering only the last event ( in time )
     * that overlaps with the break event
     *
     * @param event the first event that overlaps in time
     * @return true if the minimum time is guaranteed, false otherwise
     */
    private boolean enoughTimeWithLastEvent ( Event event ) {
        Travel path = event.getFeasiblePath();
        return minimumTime <= Math.min( getEndingTime().getEpochSecond(), event.getStartingTime().getEpochSecond() ) -
                Math.max( path.getEndingTime().getEpochSecond(), getStartingTime().getEpochSecond() ) ||
                minimumTime <= Duration.between( event.getEndingTime(), getEndingTime() ).getSeconds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List < GenericEvent > swap ( PathManager pathManager ) throws GMapsGeneralException {
        return pathManager.swapBreakEvent( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOverlapFreeIntoSchedule ( ScheduleManager scheduleManager ) {
        return scheduleManager.isBreakOverlapFreeIntoSchedule( this, false );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BreakEvent nextPeriodicEvent () {
        Instant startingTime = this.getStartingTime()
                .plus( this.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS );
        Instant endingTime = this.getEndingTime()
                .plus( this.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS );
        return new BreakEvent( this.getName(), startingTime, endingTime, false,
                this.getPeriodicity(), this.minimumTime );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInUserList ( User user ) {
        user.addBreak( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serializeResponse ( EventsListResponse eventsListResponse ) {
        eventsListResponse.addUpdatedBreakEvents( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventAndModifyFollowingEvent ( EventManager eventManager ) {
        eventManager.addBreakEvent( this );
    }

    /**
     * {@inheritDoc}
     * Obviously, since break events doesn't have feasible paths, this method simply do nothing
     */
    @Override
    public void removeFeasiblePath () {
    }
}
