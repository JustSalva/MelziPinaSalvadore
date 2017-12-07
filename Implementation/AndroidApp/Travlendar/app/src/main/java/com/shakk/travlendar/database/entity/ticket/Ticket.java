package com.shakk.travlendar.database.entity.ticket;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.shakk.travlendar.database.converters.TicketTypeConverters;

@Entity
public class Ticket {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private float cost;

    @TypeConverters(TicketTypeConverters.class)
    private TicketType type;
    @Embedded
    private PeriodTicket periodTicket;
    @Embedded
    private DistanceTicket distanceTicket;
    @Embedded
    private GeneralTicket generalTicket;

    public Ticket(float cost, PeriodTicket periodTicket) {
        this.cost = cost;
        this.type = TicketType.PERIOD;
        this.periodTicket = periodTicket;
    }

    public Ticket(float cost, DistanceTicket distanceTicket) {
        this.cost = cost;
        this.type = TicketType.DISTANCE;
        this.distanceTicket = distanceTicket;
    }

    public Ticket(float cost, GeneralTicket generalTicket) {
        this.cost = cost;
        this.type = TicketType.GENERAL;
        this.generalTicket = generalTicket;
    }

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

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
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

    public enum TicketType {
        GENERAL("General"),
        DISTANCE("Distance"),
        PERIOD("Period"),
        PATH("Path");

        private String type;

        TicketType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}

