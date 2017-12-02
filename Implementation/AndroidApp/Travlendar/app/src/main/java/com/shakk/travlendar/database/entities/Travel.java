package com.shakk.travlendar.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(
        entity = GenericEvent.class,
        parentColumns = "id",
        childColumns = "event_id",
        onDelete = CASCADE,
        onUpdate = CASCADE
))
public class Travel {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String description;
    @ColumnInfo(name = "event_id")
    private int eventId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
