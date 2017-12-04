package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "ABSTRACT_TICKET")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TICKET_TYPE")
public abstract class Ticket extends EntityWithLongKey {

    @Column(name = "COST")
    private float cost;

    @OneToMany
    @JoinColumn(name="RELATED_TO")
    private List<PublicTravelMean> relatedTo;

    @Embedded
    private Timestamp lastUpdate;

    public Ticket() {
        this.lastUpdate = new Timestamp();
    }

    public Ticket(float cost, ArrayList<PublicTravelMean> relatedTo) {
        this.cost = cost;
        this.relatedTo = relatedTo;
        this.lastUpdate = new Timestamp();
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public List<PublicTravelMean> getRelatedTo() {
        return Collections.unmodifiableList(relatedTo);
    }

    public void setRelatedTo(ArrayList<PublicTravelMean> relatedTo) {
        this.relatedTo = relatedTo;
    }

    public void addTravelMean(PublicTravelMean travelMean) {
        this.relatedTo.add(travelMean);
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
