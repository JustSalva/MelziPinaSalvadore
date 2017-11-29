package it.polimi.travlendarplus.entity;

import java.time.LocalTime;

public class BreakEvent extends GenericEvent {
    private int minimumTime;
    private User user;

    public BreakEvent(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, Period periodicity, DateOfCalendar date, int minimumTime, User user) {
        super(name, startingTime, endingTime, isScheduled, periodicity, date);
        this.minimumTime = minimumTime;
        this.user = user;
    }

    //constructor for generic event with no periodicity
    public BreakEvent(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, DateOfCalendar date, int minimumTime, User user) {
        super(name, startingTime, endingTime, isScheduled, date);
        this.minimumTime = minimumTime;
        this.user = user;
    }

    public int getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime(int minimumTime) {
        this.minimumTime = minimumTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
