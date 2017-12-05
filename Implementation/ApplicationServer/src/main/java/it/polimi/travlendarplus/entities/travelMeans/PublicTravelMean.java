package it.polimi.travlendarplus.entities.travelMeans;

import it.polimi.travlendarplus.entities.GenericEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="PUBLIC_TRAVEL_MEAN")
@DiscriminatorValue("PUBLIC")
public class PublicTravelMean extends TravelMean {

    private static final long serialVersionUID = 4896066127608298475L;

    public PublicTravelMean() {
    }

    public PublicTravelMean(String name, int speed, float eco) {
        super(name, speed, eco);
    }

    public static PublicTravelMean load(long key){
        return GenericEntity.load( PublicTravelMean.class, key );
    }
}
