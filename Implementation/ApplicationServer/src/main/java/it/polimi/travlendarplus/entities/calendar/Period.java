package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GeneralEntity;
import it.polimi.travlendarplus.entities.Timestamp;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity(name = "PERIOD")
public class Period extends GeneralEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long Id;

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
        return Id;
    }

    public void setId(long id) {
        Id = id;
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
        return GeneralEntity.load( Period.class, key );
    }
}
