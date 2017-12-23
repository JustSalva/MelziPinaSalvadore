package it.polimi.travlendarplus.retrofit.body.ticket;


import java.util.List;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.Position;
import it.polimi.travlendarplus.TravelMeanUsed;

public class PathTicketBody extends GenericTicketBody {

    private Position startingLocation;
    private Position endingLocation;

    public PathTicketBody(
            float cost,
            List<TravelMeanUsed> relatedTo,
            String lineName,
            Position startingLocation,
            Position endingLocation) {
        super(cost, relatedTo, lineName);
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
    }
}
