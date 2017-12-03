package it.polimi.travlendarplus.entities.travelMeans;

import it.polimi.travlendarplus.entities.GeneralEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

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

    public static SharingTravelMean load(long key){
        return GeneralEntity.load( SharingTravelMean.class, key );
    }
}
