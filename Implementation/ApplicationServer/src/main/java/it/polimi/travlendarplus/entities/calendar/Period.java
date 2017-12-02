package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.Timestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "PERIOD")
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String Id;

    @Column(name = "STARTING_DAY")
    private LocalDate startingDay;

    @Column(name = "ENDING_DAY")
    private LocalDate endingDay;

    @Column(name = "DELTA_DAYS")
    private int deltaDays;

    @Embedded
    private Timestamp lastUpdate;

    public Period() {
    }

    public Period(LocalDate startingDay, LocalDate endingDay, int deltaDays) {
        this.startingDay = startingDay;
        this.endingDay = endingDay;
        this.deltaDays = deltaDays;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public LocalDate getStartingDay() {
        return startingDay;
    }

    public LocalDate getEndingDay() {
        return endingDay;
    }

    public int getDeltaDays() {
        return deltaDays;
    }

    public void setStartingDay(LocalDate startingDay) {
        this.startingDay = startingDay;
    }

    public void setEndingDay(LocalDate endingDay) {
        this.endingDay = endingDay;
    }

    public void setDeltaDays(int deltaDays) {
        this.deltaDays = deltaDays;
    }
}
