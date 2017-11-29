package it.polimi.travlendarplus.entity;

import java.util.ArrayList;

public abstract class Ticket {
    private float cost;
    private ArrayList<PublicTravelMean> relatedTo;

    public Ticket(float cost, ArrayList<PublicTravelMean> relatedTo) {
        this.cost = cost;
        this.relatedTo = relatedTo;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setRelatedTo(ArrayList<PublicTravelMean> relatedTo) {
        this.relatedTo = relatedTo;
    }
}
