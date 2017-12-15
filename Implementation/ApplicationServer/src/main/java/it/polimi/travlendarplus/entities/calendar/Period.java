package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;

@Entity( name = "PERIOD" )
public class Period extends EntityWithLongKey {

    private static final long serialVersionUID = -7420043997157726131L;

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

    public Period() {
        this.lastUpdate = new Timestamp();
    }

    public Period( Instant startingDay, Instant endingDay, int deltaDays ) {
        this.startingDay = startingDay;
        this.endingDay = endingDay;
        this.deltaDays = deltaDays;
        this.lastUpdate = new Timestamp();
    }

    public static Period load( long key ) throws EntityNotFoundException {
        return GenericEntity.load( Period.class, key );
    }

    public Instant getStartingDay() {
        return startingDay;
    }

    public void setStartingDay( Instant startingDay ) {
        this.startingDay = startingDay;
    }

    public Instant getEndingDay() {
        return endingDay;
    }

    public void setEndingDay( Instant endingDay ) {
        this.endingDay = endingDay;
    }

    public int getDeltaDays() {
        return deltaDays;
    }

    public void setDeltaDays( int deltaDays ) {
        this.deltaDays = deltaDays;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate( Timestamp lastUpdate ) {
        this.lastUpdate = lastUpdate;
    }

    public long getLastPropagatedEvent() {
        return lastPropagatedEvent;
    }

    public void setLastPropagatedEvent( long lastPropagatedEvent ) {
        this.lastPropagatedEvent = lastPropagatedEvent;
    }
}
