package it.polimi.travlendarplus.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Ticket {
    private float cost;
    private List<PublicTravelMean> relatedTo;

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

    public List<PublicTravelMean> getRelatedTo() {
        return Collections.unmodifiableList(relatedTo);
    }

    public void setRelatedTo(ArrayList<PublicTravelMean> relatedTo) {
        this.relatedTo = relatedTo;
    }

    public void addTravelMean(PublicTravelMean travelMean) {
        this.relatedTo.add(travelMean);
    }
}
