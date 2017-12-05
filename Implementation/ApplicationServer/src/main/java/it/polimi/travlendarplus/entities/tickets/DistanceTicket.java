package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;

import javax.persistence.*;
import java.util.ArrayList;

@Entity(name="DISTANCE_TICKET")
@DiscriminatorValue("DISTANCE")
public class DistanceTicket extends Ticket {

    private static final long serialVersionUID = 2628317705098639359L;

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
        return GenericEntity.load( DistanceTicket.class, key );
    }
}
