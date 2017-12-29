package it.polimi.travlendarplus.database.entity.ticket;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import it.polimi.travlendarplus.database.converters.TicketTypeConverters;

/**
 * DB ticket entity.
 */
@Entity
public class Ticket {
    @PrimaryKey
    private long id;
    private float cost;

    @TypeConverters(TicketTypeConverters.class)
    private TicketType type;
    @Embedded
    private PeriodTicket periodTicket;
    @Embedded
    private DistanceTicket distanceTicket;
    @Embedded
    private GenericTicket genericTicket;

    public Ticket(long id, float cost) {
        this.id = id;
        this.cost = cost;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        this.type = TicketType.PERIOD;
    }

    public DistanceTicket getDistanceTicket() {
        return distanceTicket;
    }

    public void setDistanceTicket(DistanceTicket distanceTicket) {
        this.distanceTicket = distanceTicket;
        this.type = TicketType.DISTANCE;
    }

    public GenericTicket getGenericTicket() {
        return genericTicket;
    }

    public void setGenericTicket(GenericTicket genericTicket) {
        this.genericTicket = genericTicket;
        this.type = TicketType.GENERIC;
    }

    public enum TicketType {
        GENERIC("Generic"),
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

