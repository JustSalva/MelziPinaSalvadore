package com.shakk.travlendar.database.entity.event;

import android.arch.persistence.room.ColumnInfo;

public class Period {

    @ColumnInfo(name = "start_date")
    private long startDate;
    @ColumnInfo(name = "end_date")
    private long endDate;
    @ColumnInfo(name = "delta_days")
    private int deltaDays;

    public Period(long startDate, long endDate, int deltaDays) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.deltaDays = deltaDays;
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
