package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.travels.Travel;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "EVENT")
@DiscriminatorValue("EVENT")
public class Event extends GenericEvent {

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PREV_LOCATION_CHOICE")
    private boolean prevLocChoice;

    @ManyToOne
    @JoinColumn(name="TYPE_OF_EVENT")
    private TypeOfEvent type;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="EVENT_LATITUDE", referencedColumnName="latitude"),
            @JoinColumn(name="EVENT_LONGITUDE", referencedColumnName="longitude")
    })
    private Location eventLocation;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="DEPARTURE_LATITUDE", referencedColumnName="latitude"),
            @JoinColumn(name="DEPARTURE_LONGITUDE", referencedColumnName="longitude")
    })
    private Location departure;

    @OneToOne
    @JoinColumn(name = "LIMITED_BY")
    private Travel feasiblePath;

    public Event() {
    }

    public Event(String name, Instant startingTime, Instant endingTime, boolean isScheduled, Period periodicity,
                 DateOfCalendar date, String description, boolean prevLocChoice, User user, TypeOfEvent type,
                 Location eventLocation, Location departure, Travel feasiblePath) {
        super(name, startingTime, endingTime, isScheduled, periodicity, date);
        this.description = description;
        this.prevLocChoice = prevLocChoice;
        //this.user = user;
        this.type = type;
        this.eventLocation = eventLocation;
        this.departure = departure;
        this.feasiblePath = feasiblePath;
    }

    //constructor for generic event with no periodicity
    public Event(String name, Instant startingTime, Instant endingTime, boolean isScheduled, DateOfCalendar date,
                 String description, boolean prevLocChoice, User user, TypeOfEvent type, Location eventLocation,
                 Location departure, Travel feasiblePath) {
        super(name, startingTime, endingTime, isScheduled, date);
        this.description = description;
        this.prevLocChoice = prevLocChoice;
        //this.user = user;
        this.type = type;
        this.eventLocation = eventLocation;
        this.departure = departure;
        this.feasiblePath = feasiblePath;
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

    @Override
    public Travel getFeasiblePath() {
        return feasiblePath;
    }

    public void setFeasiblePath(Travel feasiblePath) {
        this.feasiblePath = feasiblePath;
    }

    public static Event load(long key){
        return GenericEntity.load( Event.class, key );
    }
}
