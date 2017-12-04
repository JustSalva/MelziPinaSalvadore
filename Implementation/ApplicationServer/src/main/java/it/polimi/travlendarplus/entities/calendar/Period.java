package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.Timestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "PERIOD")
public class Period extends EntityWithLongKey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "STARTING_DAY")
    private Instant startingDay;

    @Column(name = "ENDING_DAY")
    private Instant endingDay;

    @Column(name = "DELTA_DAYS")
    private int deltaDays;

    @Embedded
    private Timestamp lastUpdate;

    public Period() {
    }

    public Period(Instant startingDay, Instant endingDay, int deltaDays) {
        this.startingDay = startingDay;
        this.endingDay = endingDay;
        this.deltaDays = deltaDays;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        id = id;
    }

    public Instant getStartingDay() {
        return startingDay;
    }

    public Instant getEndingDay() {
        return endingDay;
    }

    public int getDeltaDays() {
        return deltaDays;
    }

    public void setStartingDay(Instant startingDay) {
        this.startingDay = startingDay;
    }

    public void setEndingDay(Instant endingDay) {
        this.endingDay = endingDay;
    }

    public void setDeltaDays(int deltaDays) {
        this.deltaDays = deltaDays;
    }

    public static Period load(long key){
        return GenericEntity.load( Period.class, key );
    }
}
