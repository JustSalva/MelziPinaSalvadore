package it.polimi.travlendarplus.entity.calendar;

import it.polimi.travlendarplus.entity.User;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalTime;

@Entity(name="BREAK_EVENT")
@DiscriminatorValue("BREAK_EVENT")
public class BreakEvent extends GenericEvent {

    @Column(name = "MINIMUM_TIME")
    private int minimumTime; //in minutes

    public BreakEvent(){

    }

    public BreakEvent(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, Period periodicity, DateOfCalendar date, int minimumTime, User user) {
        super(name, startingTime, endingTime, isScheduled, periodicity, date);
        this.minimumTime = minimumTime;
    }

    //constructor for generic event with no periodicity
    public BreakEvent(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, DateOfCalendar date, int minimumTime, User user) {
        super(name, startingTime, endingTime, isScheduled, date);
        this.minimumTime = minimumTime;
    }

    public int getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime(int minimumTime) {
        this.minimumTime = minimumTime;
    }

}
