package it.polimi.travlendarplus.database.entity.event;

import android.arch.persistence.room.ColumnInfo;

/**
 * DB event entity.
 */
public class Event {

    private String description;
    @ColumnInfo(name = "type_of_event_id")
    private long typeOfEventId;
    @ColumnInfo(name = "event_location")
    private String eventLocation;
    @ColumnInfo(name = "previous_location_choice")
    private boolean previousLocationChoice;
    @ColumnInfo(name = "travel_at_last_choice")
    private boolean travelAtLastChoice;
    @ColumnInfo(name = "departure_location")
    private String departureLocation;

    public Event(
            String description,
            long typeOfEventId,
            String eventLocation,
            boolean previousLocationChoice,
            boolean travelAtLastChoice,
            String departureLocation) {
        this.description = description;
        this.typeOfEventId = typeOfEventId;
        this.eventLocation = eventLocation;
        this.previousLocationChoice = previousLocationChoice;
        this.travelAtLastChoice = travelAtLastChoice;
        this.departureLocation = departureLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTypeOfEventId() {
        return typeOfEventId;
    }

    public void setTypeOfEventId(long typeOfEventId) {
        this.typeOfEventId = typeOfEventId;
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

    public boolean isTravelAtLastChoice() {
        return travelAtLastChoice;
    }

    public void setTravelAtLastChoice(boolean travelAtLastChoice) {
        this.travelAtLastChoice = travelAtLastChoice;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }
}
