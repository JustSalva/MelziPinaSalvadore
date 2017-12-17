package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.IncompatibleTravelMeansException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity( name = "ABSTRACT_TICKET" )
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn( name = "TICKET_TYPE" )
public abstract class Ticket extends EntityWithLongKey {

    private static final long serialVersionUID = 8914735050811003828L;

    @Column( name = "COST" )
    private float cost;

    @OneToMany( fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
    @JoinColumn( name = "RELATED_TO" )
    private List < PublicTravelMean > relatedTo;

    @JoinTable( name = "LINKED_PATHS" )
    @OneToMany( fetch = FetchType.LAZY )
    private List < TravelComponent > linkedTravels;

    @Embedded
    private Timestamp lastUpdate;

    public Ticket () {
        this.lastUpdate = new Timestamp();
        this.relatedTo = new ArrayList <>();
        this.linkedTravels = new ArrayList <>();
    }

    public Ticket ( float cost, ArrayList < PublicTravelMean > relatedTo ) {
        this();
        this.cost = cost;
        this.relatedTo = relatedTo;
    }

    public float getCost () {
        return cost;
    }

    public void setCost ( float cost ) {
        this.cost = cost;
    }

    public List < PublicTravelMean > getRelatedTo () {
        return Collections.unmodifiableList( relatedTo );
    }

    public void setRelatedTo ( List < PublicTravelMean > relatedTo ) {
        this.relatedTo = relatedTo;
    }

    public void setRelatedTo ( ArrayList < PublicTravelMean > relatedTo ) {
        this.relatedTo = relatedTo;
    }

    public void addTravelMean ( PublicTravelMean travelMean ) {
        this.relatedTo.add( travelMean );
    }

    public Timestamp getLastUpdate () {
        return lastUpdate;
    }

    public void setLastUpdate ( Timestamp lastUpdate ) {
        this.lastUpdate = lastUpdate;
    }

    public List < TravelComponent > getLinkedTravels () {
        return linkedTravels;
    }

    public void setLinkedTravels ( List < TravelComponent > linkedTravels ) {
        this.linkedTravels = linkedTravels;
    }

    public void addLinkedTravel ( TravelComponent travelComponent ) throws IncompatibleTravelMeansException {
        //TODO check that travel component is in public travel means
        PublicTravelMean toBeLinked = relatedTo.stream().filter( publicTravelMean -> publicTravelMean.getType()
                .equals( travelComponent.getMeanUsed().getType() ) )
                .findFirst().orElse( null );
        if ( toBeLinked == null ) {
            throw new IncompatibleTravelMeansException();
        }
        linkedTravels.add( travelComponent );
    }

    public void removeLinkedTravel ( long travelId ) {
        linkedTravels.removeIf( travelComponent -> travelComponent.getId() == travelId );
    }
}
