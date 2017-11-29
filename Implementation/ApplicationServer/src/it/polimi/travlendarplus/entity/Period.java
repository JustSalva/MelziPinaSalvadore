package it.polimi.travlendarplus.entity;

import java.time.LocalDate;

public class Period {
    private LocalDate startingDay;
    private LocalDate endingDay;
    private int deltaDays;

    public Period(LocalDate startingDay, LocalDate endingDay, int deltaDays) {
        this.startingDay = startingDay;
        this.endingDay = endingDay;
        this.deltaDays = deltaDays;
    }

    public LocalDate getStartingDay() {
        return startingDay;
    }

    public LocalDate getEndingDay() {
        return endingDay;
    }

    public int getDeltaDays() {
        return deltaDays;
    }

    public void setStartingDay(LocalDate startingDay) {
        this.startingDay = startingDay;
    }

    public void setEndingDay(LocalDate endingDay) {
        this.endingDay = endingDay;
    }

    public void setDeltaDays(int deltaDays) {
        this.deltaDays = deltaDays;
    }
}
