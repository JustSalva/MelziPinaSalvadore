package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages.TypeOfEventResponse;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * This JPA class represent a constraint on the travelled distance of a travel mean
 */
@Entity( name = "DISTANCE_CONSTRAINT" )
@DiscriminatorValue( "DISTANCE" )
public class DistanceConstraint extends Constraint {

    private static final long serialVersionUID = 2427754811507181606L;

    /**
     * Minimum length of a travel mean path
     */
    @Column( name = "MIN_LENGTH" )
    private int minLength;

    /**
     * Maximum length of a travel mean path
     */
    @Column( name = "MAX_LENGTH" )
    private int maxLength;

    public DistanceConstraint ( TravelMeanEnum concerns, int minLength, int maxLength ) {
        super( concerns );
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public DistanceConstraint () {
    }

    /**
     * Allows to load a DistanceConstraint class from the database
     *
     * @param key primary key of the distanceConstraint tuple
     * @return the requested tuple as a DistanceConstraint class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static DistanceConstraint load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( DistanceConstraint.class, key );
    }

    public int getMinLength () {
        return minLength;
    }

    public void setMinLength ( int minLength ) {
        this.minLength = minLength;
    }

    public int getMaxLength () {
        return maxLength;
    }

    public void setMaxLength ( int maxLength ) {
        this.maxLength = maxLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean respectConstraint ( TravelComponent travelComponent ) {
        return travelComponent.getLength() >= minLength && travelComponent.getLength() <= maxLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serializeResponse ( TypeOfEventResponse typeOfEventResponse ) {
        typeOfEventResponse.addDistanceConstraint( this );
    }
}
