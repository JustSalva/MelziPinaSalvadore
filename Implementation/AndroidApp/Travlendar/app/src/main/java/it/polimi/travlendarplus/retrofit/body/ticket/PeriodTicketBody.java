package it.polimi.travlendarplus.retrofit.body.ticket;


import java.util.List;

import it.polimi.travlendarplus.TravelMeanUsed;

public class PeriodTicketBody extends TicketBody {
    public PeriodTicketBody(float cost, List<TravelMeanUsed> relatedTo) {
        super(cost, relatedTo);
    }
}
