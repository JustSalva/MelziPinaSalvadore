package com.shakk.travlendar.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDate;

@Entity
public class Period {
    @PrimaryKey
    private String ID;

    @ColumnInfo(name = "start_date")
    private LocalDate startDate;
    @ColumnInfo(name = "end_date")
    private LocalDate endDate;
    @ColumnInfo(name = "delta_days")
    private int deltaDays;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public int getDeltaDays() {
        return deltaDays;
    }

    public void setDeltaDays(int deltaDays) {
        this.deltaDays = deltaDays;
    }
}
