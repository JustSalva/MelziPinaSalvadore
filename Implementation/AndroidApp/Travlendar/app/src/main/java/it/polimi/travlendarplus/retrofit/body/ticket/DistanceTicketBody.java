package it.polimi.travlendarplus.retrofit.body.ticket;


import java.util.List;

import it.polimi.travlendarplus.TravelMeanUsed;

public class DistanceTicketBody extends TicketBody {

    private int distance;

    public DistanceTicketBody(float cost, List<TravelMeanUsed> relatedTo, int distance) {
        super(cost, relatedTo);
        this.distance = distance;
    }
}
