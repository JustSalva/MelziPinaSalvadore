package it.polimi.travlendarplus.entities.travelMeans;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity( name = "PUBLIC_TRAVEL_MEAN" )
@DiscriminatorValue( "PUBLIC" )
public class PublicTravelMean extends TravelMean {

    private static final long serialVersionUID = 4896066127608298475L;

    public PublicTravelMean () {
    }

    public PublicTravelMean ( String name, TravelMeanEnum type, float eco ) {
        super( name, type, eco );
    }

    public static PublicTravelMean load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( PublicTravelMean.class, key );
    }
}
