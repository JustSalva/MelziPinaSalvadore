package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;

@Entity( name = "PERIOD" )
@TableGenerator( name = "periodId", initialValue = 10 )
public class Period extends GenericEntity {

    private static final long serialVersionUID = -7420043997157726131L;

    @Id
    @GeneratedValue( strategy = GenerationType.TABLE, generator = "periodId" )
    private long id;

    @Column( name = "STARTING_DAY" )
    private Instant startingDay;

    @Column( name = "ENDING_DAY" )
    private Instant endingDay;

    @Column( name = "DELTA_DAYS" )
    private int deltaDays;

    @Embedded
    private Timestamp lastUpdate;

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
