package it.polimi.travlendarplus.retrofit.body.ticket;


import java.util.List;

import it.polimi.travlendarplus.TravelMeanUsed;

public class GenericTicketBody extends TicketBody {

    private String lineName;

    public GenericTicketBody(float cost, List<TravelMeanUsed> relatedTo, String lineName) {
        super(cost, relatedTo);
        this.lineName = lineName;
    }
}
