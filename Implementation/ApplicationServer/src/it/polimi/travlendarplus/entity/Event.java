package it.polimi.travlendarplus.entity;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event extends GenericEvent {
    private String description;
    private boolean prevLocChoice;
    private User user;
    private TypeOfEvent type;
    private Location eventLocation;
    private Location departure;
    private ArrayList<Travel> feasiblePaths;

    public Event(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, Period periodicity, DateOfCalendar date, String description, boolean prevLocChoice, User user, TypeOfEvent type, Location eventLocation, Location departure, ArrayList<Travel> feasiblePaths) {
        super(name, startingTime, endingTime, isScheduled, periodicity, date);
        this.description = description;
        this.prevLocChoice = prevLocChoice;
        this.user = user;
        this.type = type;
        this.eventLocation = eventLocation;
        this.departure = departure;
        this.feasiblePaths = feasiblePaths;
    }

    //constructor for generic event with no periodicity
    public Event(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, DateOfCalendar date, String description, boolean prevLocChoice, User user, TypeOfEvent type, Location eventLocation, Location departure, ArrayList<Travel> feasiblePaths) {
        super(name, startingTime, endingTime, isScheduled, date);
        this.description = description;
        this.prevLocChoice = prevLocChoice;
        this.user = user;
        this.type = type;
        this.eventLocation = eventLocation;
        this.departure = departure;
        this.feasiblePaths = feasiblePaths;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrevLocChoice() {
        return prevLocChoice;
    }

    public void setPrevLocChoice(boolean prevLocChoice) {
        this.prevLocChoice = prevLocChoice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TypeOfEvent getType() {
        return type;
    }

    public void setType(TypeOfEvent type) {
        this.type = type;
    }

    public Location getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(Location eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Location getDeparture() {
        return departure;
    }

    public void setDeparture(Location departure) {
        this.departure = departure;
    }

    public void setFeasiblePaths(ArrayList<Travel> feasiblePaths) {
        this.feasiblePaths = feasiblePaths;
    }
}
