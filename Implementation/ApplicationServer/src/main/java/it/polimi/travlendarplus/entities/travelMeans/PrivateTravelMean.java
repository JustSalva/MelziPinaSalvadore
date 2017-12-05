package it.polimi.travlendarplus.entities.travelMeans;

import it.polimi.travlendarplus.entities.GenericEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="PRIVATE_TRAVEL_MEAN")
@DiscriminatorValue("PRIVATE")
public class PrivateTravelMean extends TravelMean{

    private static final long serialVersionUID = 2490349679121999838L;

    public PrivateTravelMean( String name, TravelMeanEnum type, float eco) {
        super(name, type, eco);
    }

    public PrivateTravelMean() {
    }

    public static PrivateTravelMean load(long key){
        return GenericEntity.load( PrivateTravelMean.class, key );
    }
}
