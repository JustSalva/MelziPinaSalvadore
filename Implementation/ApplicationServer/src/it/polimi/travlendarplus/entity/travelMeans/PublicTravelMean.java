package it.polimi.travlendarplus.entity.travelMeans;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="PUBLIC_TRAVEL_MEAN")
@DiscriminatorValue("PUBLIC")
public class PublicTravelMean extends TravelMean {

    public PublicTravelMean() {
    }

    public PublicTravelMean(String name, int speed, float eco) {
        super(name, speed, eco);
    }
}
