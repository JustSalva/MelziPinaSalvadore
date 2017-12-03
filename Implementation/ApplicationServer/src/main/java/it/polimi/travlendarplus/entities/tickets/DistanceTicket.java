package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GeneralEntity;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;

import javax.persistence.*;
import java.util.ArrayList;

@Entity(name="DISTANCE_TICKET")
@DiscriminatorValue("DISTANCE")
public class DistanceTicket extends Ticket {

    @Column(name = "DISTANCE")
    private int distance;

    public DistanceTicket() {
    }

    public DistanceTicket(float cost, ArrayList<PublicTravelMean> relatedTo, int distance) {
        super(cost, relatedTo);
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public static DistanceTicket load(long key){
        return GeneralEntity.load( DistanceTicket.class, key );
    }
}
