package it.polimi.travlendarplus.entity.tickets;

import it.polimi.travlendarplus.entity.travelMeans.PublicTravelMean;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
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
}
