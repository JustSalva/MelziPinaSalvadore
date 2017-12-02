package com.shakk.travlendar.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDate;
import java.util.List;


@Entity
public class Ticket {
    @PrimaryKey
    private int ID;
    private float cost;
    @ColumnInfo(name = "travel_components")
    private List<TravelComponent> travelComponents;
    @ColumnInfo(name = "type_of_ticket")
    private TicketType ticketType;

    /*
        GENERAL TICKET FIELDS
     */
    @ColumnInfo(name = "line_name")
    private String lineName;

    /*
        PATH TICKET FIELDS
     */
    @ColumnInfo(name = "departure_location")
    private String departureLocation;
    @ColumnInfo(name = "arrival_location")
    private String arrivalLocation;

    /*
        DISTANCE TICKET FIELDS
     */
    private float distance;

    /*
        PERIOD TICKET FIELDS
     */
    private String name;
    @ColumnInfo(name = "start_date")
    private LocalDate startDate;
    @ColumnInfo(name = "end_date")
    private LocalDate endDate;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public List<TravelComponent> getTravelComponents() {
        return travelComponents;
    }

    public void setTravelComponents(List<TravelComponent> travelComponents) {
        this.travelComponents = travelComponents;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
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

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
