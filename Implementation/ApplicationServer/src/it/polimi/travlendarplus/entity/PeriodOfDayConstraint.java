package it.polimi.travlendarplus.entity;

import java.time.LocalTime;

public class PeriodOfDayConstraint extends Constraint {
    private LocalTime minHour;
    private LocalTime maxHour;

    public PeriodOfDayConstraint(TravelMean concerns, LocalTime minHour, LocalTime maxHour) {
        super(concerns);
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    public LocalTime getMinHour() {
        return minHour;
    }

    public void setMinHour(LocalTime minHour) {
        this.minHour = minHour;
    }

    public LocalTime getMaxHour() {
        return maxHour;
    }

    public void setMaxHour(LocalTime maxHour) {
        this.maxHour = maxHour;
    }
}
