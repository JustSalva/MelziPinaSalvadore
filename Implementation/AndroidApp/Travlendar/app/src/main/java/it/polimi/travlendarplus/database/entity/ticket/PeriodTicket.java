package it.polimi.travlendarplus.database.entity.ticket;

import android.arch.persistence.room.ColumnInfo;

/**
 * DB period ticket entity.
 */
public class PeriodTicket {
    private String name;
    @ColumnInfo(name = "start_date")
    private long startDate;
    @ColumnInfo(name = "end_date")
    private long endDate;

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
}
