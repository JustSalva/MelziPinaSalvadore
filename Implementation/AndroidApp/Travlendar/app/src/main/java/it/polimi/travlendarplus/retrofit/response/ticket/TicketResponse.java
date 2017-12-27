package it.polimi.travlendarplus.retrofit.response.ticket;


import java.util.List;

import it.polimi.travlendarplus.TravelMeanUsed;

public class TicketResponse {

    private long id;
    private float cost;
    private List<TravelMeanUsed> relatedTo;

    public long getId() {
        return id;
    }

    public float getCost() {
        return cost;
    }

    public List<TravelMeanUsed> getRelatedTo() {
        return relatedTo;
    }

    @Override
    public String toString() {
        return "Cost("+cost+") ";
    }
}
