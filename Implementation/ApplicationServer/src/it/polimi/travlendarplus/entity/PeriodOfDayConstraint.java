package it.polimi.travlendarplus.entity;

import java.sql.Time;

public class PeriodOfDayConstraint extends Constraint {
    private Time minHour;
    private Time maxHour;

    public PeriodOfDayConstraint(TravelMean concerns, Time minHour, Time maxHour) {
        super(concerns);
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    public Time getMinHour() {
        return minHour;
    }

    public void setMinHour(Time minHour) {
        this.minHour = minHour;
    }

    public Time getMaxHour() {
        return maxHour;
    }

    public void setMaxHour(Time maxHour) {
        this.maxHour = maxHour;
    }
}
