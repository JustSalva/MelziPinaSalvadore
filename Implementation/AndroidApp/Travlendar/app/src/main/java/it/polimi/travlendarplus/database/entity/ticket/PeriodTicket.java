package it.polimi.travlendarplus.database.entity.ticket;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.TypeConverters;

import it.polimi.travlendarplus.database.converters.TicketTypeConverters;

/**
 * DB period ticket entity.
 */
public class PeriodTicket {
    private String name;
    @ColumnInfo(name = "start_date")
    private long startDate;
    @ColumnInfo(name = "end_date")
    private long endDate;

    @TypeConverters(TicketTypeConverters.class)
    @ColumnInfo(name = "decorator_type")
    private Ticket.TicketType decoratorType;

    public PeriodTicket(String name, long startDate, long endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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

    public Ticket.TicketType getDecoratorType() {
        return decoratorType;
    }

    public void setDecoratorType(Ticket.TicketType decoratorType) {
        this.decoratorType = decoratorType;
    }
}
