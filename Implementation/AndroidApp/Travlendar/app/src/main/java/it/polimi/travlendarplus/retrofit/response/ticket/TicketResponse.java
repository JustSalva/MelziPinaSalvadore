package it.polimi.travlendarplus.retrofit.response.ticket;


import java.util.List;

import it.polimi.travlendarplus.MiniTravel;
import it.polimi.travlendarplus.TravelMeanUsed;
import it.polimi.travlendarplus.database.entity.TravelComponent;

public class TicketResponse {

    private long id;
    private float cost;
    private List<TravelMeanUsed> relatedTo;
    private List<MiniTravel> linkedTravels;

    public long getId() {
        return id;
    }

    public float getCost() {
        return cost;
    }

    public List<TravelMeanUsed> getRelatedTo() {
        return relatedTo;
    }

    public List<MiniTravel> getLinkedTravels() {
        return linkedTravels;
    }

    @Override
    public String toString() {
        return "Cost("+cost+" EUR)\n";
    }
}
