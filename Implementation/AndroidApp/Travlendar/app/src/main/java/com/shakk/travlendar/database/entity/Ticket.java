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
public class Ticket {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private float cost;
    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "ticket_type")
    private String ticketType;
    @Embedded
    private PeriodTicket periodTicket;
    @Embedded
    private DistanceTicket distanceTicket;
    @Embedded
    private GeneralTicket generalTicket;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public PeriodTicket getPeriodTicket() {
        return periodTicket;
    }

    public void setPeriodTicket(PeriodTicket periodTicket) {
        this.periodTicket = periodTicket;
    }

    public DistanceTicket getDistanceTicket() {
        return distanceTicket;
    }

    public void setDistanceTicket(DistanceTicket distanceTicket) {
        this.distanceTicket = distanceTicket;
    }

    public GeneralTicket getGeneralTicket() {
        return generalTicket;
    }

    public void setGeneralTicket(GeneralTicket generalTicket) {
        this.generalTicket = generalTicket;
    }
}

class PeriodTicket {
    private String name;
    @ColumnInfo(name = "start_date")
    private long startDate;
    @ColumnInfo(name = "end_date")
    private long endDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}

class DistanceTicket {
    private float distance;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}

class GeneralTicket {
    @ColumnInfo(name = "line_name")
    private String lineName;

    @Embedded
    private PathTicket pathTicket;

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public PathTicket getPathTicket() {
        return pathTicket;
    }

    public void setPathTicket(PathTicket pathTicket) {
        this.pathTicket = pathTicket;
    }
}

class PathTicket {
    @ColumnInfo(name = "departure_location")
    private String departureLocation;
    @ColumnInfo(name = "arrival_location")
    private String arrivalLocation;

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
}
