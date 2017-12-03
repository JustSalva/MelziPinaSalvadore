package com.shakk.travlendar.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "user_id",
        onDelete = CASCADE,
        onUpdate = CASCADE
))
public class GenericEvent {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private long date;
    @ColumnInfo(name = "start_time")
    private long startTime;
    @ColumnInfo(name = "end_time")
    private long endTime;
    @ColumnInfo(name = "is_scheduled")
    private boolean isScheduled;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "event_type")
    private String eventType;

    @Embedded
    private Event event;
    @Embedded
    private BreakEvent breakEvent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public BreakEvent getBreakEvent() {
        return breakEvent;
    }

    public void setBreakEvent(BreakEvent breakEvent) {
        this.breakEvent = breakEvent;
    }
}

class Event {
    private String description;
    private String type;
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
}

class BreakEvent {
    @ColumnInfo(name = "minimum_time")
    private long minimumTime;

    public long getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime(long minimumTime) {
        this.minimumTime = minimumTime;
    }
}
