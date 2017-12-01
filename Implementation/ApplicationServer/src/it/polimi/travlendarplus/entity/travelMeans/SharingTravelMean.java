package it.polimi.travlendarplus.entity.travelMeans;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="SHARING_TRAVEL_MEAN")
@DiscriminatorValue("SHARING")
public class SharingTravelMean extends TravelMean{

    private float costOnTime;

    public SharingTravelMean() {
    }

    public SharingTravelMean(String name, int speed, float eco, float costOnTime) {
        super(name, speed, eco);
        this.costOnTime = costOnTime;
    }

    public float getCostOnTime() {
        return costOnTime;
    }

    public void setCostOnTime(float costOnTime) {
        this.costOnTime = costOnTime;
    }
}
