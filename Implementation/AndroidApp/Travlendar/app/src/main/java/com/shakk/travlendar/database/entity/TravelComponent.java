package com.shakk.travlendar.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.shakk.travlendar.database.entity.event.GenericEvent;
import com.shakk.travlendar.database.entity.ticket.Ticket;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.SET_NULL;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = GenericEvent.class,
                parentColumns = "id",
                childColumns = "event_id",
                onUpdate = CASCADE,
                onDelete = CASCADE
        ),
        @ForeignKey(
                entity = Ticket.class,
                parentColumns = "id",
                childColumns = "ticket_id",
                onUpdate = CASCADE,
                onDelete = SET_NULL
        )
}, tableName = "travel_component")
public class TravelComponent {

    @PrimaryKey
    private long id;

    private float length;
    @ColumnInfo(name = "event_id")
    private int eventId;

    @ColumnInfo(name = "ticket_id")
    private int ticketId;

    @ColumnInfo(name = "travel_mean")
    private String travelMean;

    @ColumnInfo(name = "departure_location")
    private String departureLocation;
    @ColumnInfo(name = "arrival_location")
    private String arrivalLocation;

    @ColumnInfo(name = "start_time")
    private String startTime;
    @ColumnInfo(name = "end_time")
    private String endTime;

    public TravelComponent(long id, float length, int eventId, int ticketId, String travelMean,
                           String departureLocation, String arrivalLocation,
                           String startTime, String endTime) {
        this.id = id;
        this.length = length;
        this.eventId = eventId;
        this.ticketId = ticketId;
        this.travelMean = travelMean;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getTravelMean() {
        return travelMean;
    }

    public void setTravelMean(String travelMean) {
        this.travelMean = travelMean;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
