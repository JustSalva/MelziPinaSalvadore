package com.shakk.travlendar.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(
        entity = GenericEvent.class,
        parentColumns = "id",
        childColumns = "event_id",
        onUpdate = CASCADE,
        onDelete = CASCADE
))
public class Period {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "event_id")
    private int eventId;
    @ColumnInfo(name = "start_date")
    private long startDate;
    @ColumnInfo(name = "end_date")
    private long endDate;
    @ColumnInfo(name = "delta_days")
    private int deltaDays;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getDeltaDays() {
        return deltaDays;
    }

    public void setDeltaDays(int deltaDays) {
        this.deltaDays = deltaDays;
    }
}
