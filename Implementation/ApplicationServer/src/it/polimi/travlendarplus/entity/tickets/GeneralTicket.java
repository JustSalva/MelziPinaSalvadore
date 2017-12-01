package it.polimi.travlendarplus.entity.tickets;

import it.polimi.travlendarplus.entity.travelMeans.PublicTravelMean;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;

@Entity(name="GENERAL_TICKET")
@DiscriminatorValue("GENERAL")
public class GeneralTicket extends Ticket {

    @Column(name = "LINE_NAME")
    private String lineName;

    public GeneralTicket() {
    }

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
