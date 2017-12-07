package com.shakk.travlendar.database.entity.event;

import android.arch.persistence.room.ColumnInfo;

public class Event {
    private String description;
    @ColumnInfo(name = "type_of_event")
    private String typeOfEvent;
    @ColumnInfo(name = "event_location")
    private String eventLocation;
    @ColumnInfo(name = "previous_location_choice")
    private boolean previousLocationChoice;
    @ColumnInfo(name = "departure_location")
    private String departureLocation;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeOfEvent() {
        return typeOfEvent;
    }

    public void setTypeOfEvent(String typeOfEvent) {
        this.typeOfEvent = typeOfEvent;
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
}
