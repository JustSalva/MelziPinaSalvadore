package it.polimi.travlendarplus.entity;

import java.util.ArrayList;

public class GeneralTicket extends Ticket {
    private String lineName;

    public GeneralTicket(float cost, ArrayList<PublicTravelMean> relatedTo, String lineName) {
        super(cost, relatedTo);
        this.lineName = lineName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
}
