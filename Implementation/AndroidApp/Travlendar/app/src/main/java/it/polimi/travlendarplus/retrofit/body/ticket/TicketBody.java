package it.polimi.travlendarplus.retrofit.body.ticket;


import java.util.List;

import it.polimi.travlendarplus.TravelMeanUsed;

public class TicketBody {

    private float cost;
    private List<TravelMeanUsed> relatedTo;

    public TicketBody(float cost, List<TravelMeanUsed> relatedTo) {
        this.cost = cost;
        this.relatedTo = relatedTo;
    }
}
