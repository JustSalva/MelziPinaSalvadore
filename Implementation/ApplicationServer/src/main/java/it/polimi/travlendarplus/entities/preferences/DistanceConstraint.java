package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;

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

    @Override
    public boolean respectConstraint( TravelComponent travelComponent ) {
        return travelComponent.getLength() >= minLenght && travelComponent.getLength() <= maxLenght;
    }

    public static DistanceConstraint load( long key) throws EntityNotFoundException {
        return GenericEntity.load( DistanceConstraint.class, key );
    }
}
