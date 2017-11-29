package it.polimi.travlendarplus.entity;

import java.util.ArrayList;

public class DistanceTicket extends Ticket {
    private int distance;

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
