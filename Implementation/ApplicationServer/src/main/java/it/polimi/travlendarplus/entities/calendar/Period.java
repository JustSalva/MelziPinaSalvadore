package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;

/**
 * This JPA class represent an event periodicity
 */
@Entity( name = "PERIOD" )
@TableGenerator( name = "periodId", initialValue = 10 )
public class Period extends GenericEntity {

    private static final long serialVersionUID = -7420043997157726131L;

    @Id
    @GeneratedValue( strategy = GenerationType.TABLE, generator = "periodId" )
    private long id;

    /**
     * Unix time at which the periodicity begins
     */
    @Column( name = "STARTING_DAY" )
    private Instant startingDay;

    /**
     * Unix time at which the periodicity ends
     */
    @Column( name = "ENDING_DAY" )
    private Instant endingDay;

    /**
     * Gap between two periodic events with this periodicity
     */
    @Column( name = "DELTA_DAYS" )
    private int deltaDays;

    /**
     * Timestamp in Unix time that memorize the last update of an event
     */
    @Embedded
    private Timestamp lastUpdate;

    /**
     * Identifier of the last propagated event, used to propagate the events with this periodicity in time
     *
     * @see it.polimi.travlendarplus.beans.calendarManager.PeriodicEventsPropagator
     */
    @Column( name = "LAST_PROPAGATED_EVENT" )
    private long lastPropagatedEvent;

    public Period () {
        this.lastUpdate = new Timestamp();
    }

    public Period ( Instant startingDay, Instant endingDay, int deltaDays ) {
        this.startingDay = startingDay;
        this.endingDay = endingDay;
        this.deltaDays = deltaDays;
        this.lastUpdate = new Timestamp();
    }

    /**
     * Allows to load a Period class from the database
     *
     * @param key primary key of the period tuple
     * @return the requested tuple as a period class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static Period load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( Period.class, key );
    }

    public Instant getStartingDay () {
        return startingDay;
    }

    public void setStartingDay ( Instant startingDay ) {
        this.startingDay = startingDay;
    }

    public Instant getEndingDay () {
        return endingDay;
    }

    public void setEndingDay ( Instant endingDay ) {
        this.endingDay = endingDay;
    }

    public int getDeltaDays () {
        return deltaDays;
    }

    public void setDeltaDays ( int deltaDays ) {
        this.deltaDays = deltaDays;
    }

    public Timestamp getLastUpdate () {
        return lastUpdate;
    }

    public void setLastUpdate ( Timestamp lastUpdate ) {
        this.lastUpdate = lastUpdate;
    }

    public long getLastPropagatedEvent () {
        return lastPropagatedEvent;
    }

    public void setLastPropagatedEvent ( long lastPropagatedEvent ) {
        this.lastPropagatedEvent = lastPropagatedEvent;
    }

    public long getId () {
        return id;
    }

    public void setId ( long id ) {
        this.id = id;
    }

    /**
     * Checks if a period instance is already present in the database
     *
     * @return true if present, false otherwise
     */
    @Override
    public boolean isAlreadyInDb () {
        try {
            load( id );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}
