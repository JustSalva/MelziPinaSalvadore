package it.polimi.travlendarplus.entity;


import java.time.LocalTime;

public abstract class GenericEvent {
    private String name;
    private LocalTime startingTime;
    private LocalTime endingTime;
    private boolean isScheduled;
    private Period periodicity;
    private DateOfCalendar date;

    public GenericEvent(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, Period periodicity, DateOfCalendar date) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.isScheduled = isScheduled;
        this.periodicity = periodicity;
        this.date = date;
    }

    //constructor for generic event with no periodicity
    public GenericEvent(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, DateOfCalendar date) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.isScheduled = isScheduled;
        this.periodicity = new Period(null,null, 0);
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalTime startingTime) {
        this.startingTime = startingTime;
    }

    public LocalTime getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(LocalTime endingTime) {
        this.endingTime = endingTime;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public Period getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Period periodicity) {
        this.periodicity = periodicity;
    }

    public DateOfCalendar getDate() {
        return date;
    }

    public void setDate(DateOfCalendar date) {
        this.date = date;
    }
}
