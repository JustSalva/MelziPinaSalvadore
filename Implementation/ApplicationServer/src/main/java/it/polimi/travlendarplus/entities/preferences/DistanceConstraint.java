package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity( name = "DISTANCE_CONSTRAINT" )
@DiscriminatorValue( "DISTANCE" )
public class DistanceConstraint extends Constraint {

    @Column( name = "MIN_LENGTH" )
    private int minLength;

    @Column( name = "MAX_LENGTH" )
    private int maxLength;

    public DistanceConstraint( TravelMeanEnum concerns, int minLength, int maxLength ) {
        super( concerns );
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public DistanceConstraint() {
    }

    public static DistanceConstraint load( long key ) throws EntityNotFoundException {
        return GenericEntity.load( DistanceConstraint.class, key );
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength( int minLength ) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength( int maxLength ) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean respectConstraint( TravelComponent travelComponent ) {
        return travelComponent.getLength() >= minLength && travelComponent.getLength() <= maxLength;
    }
}
