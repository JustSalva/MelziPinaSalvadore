package it.polimi.travlendarplus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalTime;

@Entity(name="BREAK_EVENT")
public class BreakEvent extends GenericEvent {

    @Column(name = "MINIMUM_TIME")
    private int minimumTime; //TODO in minutes?

    //private User user; TODO why?

    public BreakEvent(){

    }

    public BreakEvent(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, Period periodicity, DateOfCalendar date, int minimumTime, User user) {
        super(name, startingTime, endingTime, isScheduled, periodicity, date);
        this.minimumTime = minimumTime;
        //this.user = user;
    }

    //constructor for generic event with no periodicity
    public BreakEvent(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, DateOfCalendar date, int minimumTime, User user) {
        super(name, startingTime, endingTime, isScheduled, date);
        this.minimumTime = minimumTime;
        //this.user = user;
    }

    public int getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime(int minimumTime) {
        this.minimumTime = minimumTime;
    }

    /*public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }*/
}
