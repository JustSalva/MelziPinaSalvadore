package it.polimi.travlendarplus.entities.travelMeans;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * This JPA class represent a private travel mean, that is any travel mean owned and used by the user
 * ( ex. personal car or bike )
 */
@Entity( name = "PRIVATE_TRAVEL_MEAN" )
@DiscriminatorValue( "PRIVATE" )
public class PrivateTravelMean extends TravelMean {

    private static final long serialVersionUID = 2490349679121999838L;

    public PrivateTravelMean ( String name, TravelMeanEnum type, float eco ) {
        super( name, type, eco );
    }

    public PrivateTravelMean () {
    }

    /**
     * Allows to load a PrivateTravelMean class from the database
     *
     * @param key primary key of the privateTravelMean tuple
     * @return the requested tuple as a PrivateTravelMean class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static PrivateTravelMean load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( PrivateTravelMean.class, key );
    }
}
