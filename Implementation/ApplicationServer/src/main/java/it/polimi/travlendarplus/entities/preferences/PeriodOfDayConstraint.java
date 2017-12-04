package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

import javax.persistence.*;

@Entity(name="PERIOD_OF_DAY_CONSTRAINT")
@DiscriminatorValue("PERIOD_OF_DAY")
public class PeriodOfDayConstraint extends Constraint {

    @Column(name = "MIN_HOUR")
    private long minHour; //In seconds from 00.00 of the day

    @Column(name = "MAX_HOUR")
    private long maxHour; //max value = 24 h

    public PeriodOfDayConstraint(TravelMeanEnum concerns, long minHour, long maxHour) {
        super(concerns);
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    public PeriodOfDayConstraint() {
    }

    public long getMinHour() {
        return minHour;
    }

    public void setMinHour(long minHour) {
        this.minHour = minHour;
    }

    public long getMaxHour() {
        return maxHour;
    }

    public void setMaxHour(long maxHour) {
        this.maxHour = maxHour;
    }

    public static PeriodOfDayConstraint load(long key){
        return GenericEntity.load( PeriodOfDayConstraint.class, key );
    }
}
