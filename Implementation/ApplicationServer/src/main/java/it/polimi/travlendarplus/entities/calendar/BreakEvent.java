package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.User;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.Instant;
import java.time.LocalTime;

@Entity(name="BREAK_EVENT")
@DiscriminatorValue("BREAK_EVENT")
public class BreakEvent extends GenericEvent {

    @Column(name = "MINIMUM_TIME")
    private long minimumTime; // in seconds

    public BreakEvent(){

    }

    public BreakEvent(String name, Instant startingTime, Instant endingTime, boolean isScheduled, Period periodicity, DateOfCalendar date, long minimumTime, User user) {
        super(name, startingTime, endingTime, isScheduled, periodicity, date);
        this.minimumTime = minimumTime;
    }

    //constructor for generic event with no periodicity
    public BreakEvent(String name, Instant startingTime, Instant endingTime, boolean isScheduled, DateOfCalendar date, long minimumTime, User user) {
        super(name, startingTime, endingTime, isScheduled, date);
        this.minimumTime = minimumTime;
    }

    public long getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime(int minimumTime) {
        this.minimumTime = minimumTime;
    }

}
