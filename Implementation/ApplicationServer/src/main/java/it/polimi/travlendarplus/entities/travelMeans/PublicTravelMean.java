package it.polimi.travlendarplus.entities.travelMeans;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * This JPA class represent a public travel mean, that is any travel mean which is shared by many individuals
 * and provided by a transport service provider (ex. bus, train , subway ...)
 */
@Entity( name = "PUBLIC_TRAVEL_MEAN" )
@DiscriminatorValue( "PUBLIC" )
public class PublicTravelMean extends TravelMean {

    private static final long serialVersionUID = 4896066127608298475L;

    public PublicTravelMean () {
    }

    public PublicTravelMean ( String name, TravelMeanEnum type ) {
        super( name, type, 0 );
    }

    public PublicTravelMean ( String name, TravelMeanEnum type, float eco ) {
        super( name, type, eco );
    }

    /**
     * Allows to load a PublicTravelMean class from the database
     *
     * @param key primary key of the publicTravelMean tuple
     * @return the requested tuple as a PublicTravelMean class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static PublicTravelMean load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( PublicTravelMean.class, key );
    }
}
