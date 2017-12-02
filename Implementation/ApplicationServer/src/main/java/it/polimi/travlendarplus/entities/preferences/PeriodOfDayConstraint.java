package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalTime;

@Entity(name="PERIOD_OF_DAY_CONSTRAINT")
@DiscriminatorValue("PERIOD_OF_DAY")
public class PeriodOfDayConstraint extends Constraint {

    @Column(name = "MIN_HOUR")
    private LocalTime minHour;

    @Column(name = "MAX_HOUR")
    private LocalTime maxHour;

    public PeriodOfDayConstraint(TravelMeanEnum concerns, LocalTime minHour, LocalTime maxHour) {
        super(concerns);
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    public PeriodOfDayConstraint() {
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
