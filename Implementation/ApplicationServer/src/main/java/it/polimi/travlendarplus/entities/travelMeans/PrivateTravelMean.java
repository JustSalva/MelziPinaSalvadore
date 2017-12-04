package it.polimi.travlendarplus.entities.travelMeans;

import it.polimi.travlendarplus.entities.GeneralEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

@Entity(name="PRIVATE_TRAVEL_MEAN")
@DiscriminatorValue("PRIVATE")
public class PrivateTravelMean extends TravelMean{

    public PrivateTravelMean(String name, int speed, float eco) {
        super(name, speed, eco);
    }

    public PrivateTravelMean() {
    }

    public static PrivateTravelMean load(long key){
        return GeneralEntity.load( PrivateTravelMean.class, key );
    }

    @Override
    public boolean isAlreadyInDb() {
        return load(id) != null;
    }
}
