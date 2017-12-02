package it.polimi.travlendarplus.entities.travelMeans;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="PRIVATE_TRAVEL_MEAN")
@DiscriminatorValue("PRIVATE")
public class PrivateTravelMean extends TravelMean{

    public PrivateTravelMean(String name, int speed, float eco) {
        super(name, speed, eco);
    }

    public PrivateTravelMean() {
    }
}
