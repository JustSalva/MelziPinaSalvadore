package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GeneralEntity;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "ABSTRACT_TICKET")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TICKET_TYPE")
public abstract class Ticket extends GeneralEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "COST")
    private float cost;

    @OneToMany
    @JoinColumn(name="RELATED_TO")
    private List<PublicTravelMean> relatedTo;

    @Embedded
    private Timestamp lastUpdate;

    public Ticket() {
    }

    public Ticket(float cost, ArrayList<PublicTravelMean> relatedTo) {
        this.cost = cost;
        this.relatedTo = relatedTo;
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
}
