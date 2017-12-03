package com.shakk.travlendar.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.SET_NULL;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Travel.class,
                parentColumns = "id",
                childColumns = "travel_id",
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
})
public class TravelComponent {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private float length;
    @ColumnInfo(name = "travel_id")
    private int travelId;

    @ColumnInfo(name = "ticket_id")
    private int ticketId;

    @ColumnInfo(name = "travel_mean")
    private String travelMean;

    @ColumnInfo(name = "departure_location")
    private String departureLocation;
    @ColumnInfo(name = "arrival_location")
    private String arrivalLocation;

    @ColumnInfo(name = "starting_time")
    private String startingTime;
    @ColumnInfo(name = "ending_time")
    private String endingTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public int getTravelId() {
        return travelId;
    }

    public void setTravelId(int travelId) {
        this.travelId = travelId;
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

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }
}
