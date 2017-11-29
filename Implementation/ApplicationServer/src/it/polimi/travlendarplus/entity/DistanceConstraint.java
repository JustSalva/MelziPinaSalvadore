package it.polimi.travlendarplus.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="DISTANCE_CONSTRAINT")
@DiscriminatorValue("DISTANCE")
public class DistanceConstraint extends Constraint{

    @Column(name = "MIN_LENGHT")
    private int minLenght;
    @Column(name = "MAX_LENGHT")
    private int maxLenght;

    public DistanceConstraint(TravelMeanEnum concerns, int minLenght, int maxLenght) {
        super(concerns);
        this.minLenght = minLenght;
        this.maxLenght = maxLenght;
    }

    public DistanceConstraint() {
    }

    public int getMinLenght() {
        return minLenght;
    }

    public void setMinLenght(int minLenght) {
        this.minLenght = minLenght;
    }

    public int getMaxLenght() {
        return maxLenght;
    }

    public void setMaxLenght(int maxLenght) {
        this.maxLenght = maxLenght;
    }
}
