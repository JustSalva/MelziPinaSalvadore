package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;

import javax.persistence.*;
import java.util.ArrayList;

@Entity(name="GENERAL_TICKET")
@DiscriminatorValue("GENERAL")
public class GenericTicket extends Ticket {

    @Column(name = "LINE_NAME")
    private String lineName;

    public GenericTicket() {
    }

    public GenericTicket(float cost, ArrayList<PublicTravelMean> relatedTo, String lineName) {
        super(cost, relatedTo);
        this.lineName = lineName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public static GenericTicket load(long key){
        return GenericEntity.load( GenericTicket.class, key );
    }
}
