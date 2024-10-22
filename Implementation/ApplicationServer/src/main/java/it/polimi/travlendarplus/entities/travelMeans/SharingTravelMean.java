package it.polimi.travlendarplus.entities.travelMeans;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * This JPA class represent a travel mean offered in sharing
 */
@Entity( name = "SHARING_TRAVEL_MEAN" )
@DiscriminatorValue( "SHARING" )
public class SharingTravelMean extends TravelMean {

    private static final long serialVersionUID = 6590895544540213424L;

    private float costOnTime;

    public SharingTravelMean () {
    }

    public SharingTravelMean ( String name, TravelMeanEnum type, float eco, float costOnTime ) {
        super( name, type, eco );
        this.costOnTime = costOnTime;
    }

    /**
     * Allows to load a SharingTravelMean class from the database
     *
     * @param key primary key of the sharingTravelMean tuple
     * @return the requested tuple as a SharingTravelMean class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static SharingTravelMean load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( SharingTravelMean.class, key );
    }

    public float getCostOnTime () {
        return costOnTime;
    }

    public void setCostOnTime ( float costOnTime ) {
        this.costOnTime = costOnTime;
    }

}
