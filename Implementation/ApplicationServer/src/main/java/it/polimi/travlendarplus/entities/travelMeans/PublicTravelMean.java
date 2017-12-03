package it.polimi.travlendarplus.entities.travelMeans;

import it.polimi.travlendarplus.entities.GeneralEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

@Entity(name="PUBLIC_TRAVEL_MEAN")
@DiscriminatorValue("PUBLIC")
public class PublicTravelMean extends TravelMean {

    public PublicTravelMean() {
    }

    public PublicTravelMean(String name, int speed, float eco) {
        super(name, speed, eco);
    }

    public static PublicTravelMean load(long key){
        return GeneralEntity.load( PublicTravelMean.class, key );
    }
}
