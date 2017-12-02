package com.shakk.travlendar.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class GenericEvent {
    @PrimaryKey
    private String ID;
    private String name;
    private LocalDate date;
    @ColumnInfo(name = "start_time")
    private LocalTime startTime;
    @ColumnInfo(name = "end_time")
    private LocalTime endTime;
    @ColumnInfo(name = "is_scheduled")
    private boolean isScheduled;
    private Period period;
    private User user;
    private EventType eventType;

    /*
      EVENT FIELDS
     */
    private String description;
    private String type;
    @ColumnInfo(name = "event_location")
    private String eventLocation;
    @ColumnInfo(name = "previous_location_choice")
    private boolean previousLocationChoice;
    @ColumnInfo(name = "departure_location")
    private String departureLocation;
    private Travel travel;

    /*
        BREAK EVENT FIELDS
     */
    @ColumnInfo(name = "minimum_time")
    private LocalTime minimumTime;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public boolean isPreviousLocationChoice() {
        return previousLocationChoice;
    }

    public void setPreviousLocationChoice(boolean previousLocationChoice) {
        this.previousLocationChoice = previousLocationChoice;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public LocalTime getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime(LocalTime minimumTime) {
        this.minimumTime = minimumTime;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
