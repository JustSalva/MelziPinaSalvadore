package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.RESTful.messages.tripMessages.PeriodTicketResponse;
import it.polimi.travlendarplus.RESTful.messages.tripMessages.TicketListResponse;
import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.IncompatibleTravelMeansException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.TicketNotValidException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This JPA abstract class represent a generic ticket, to be implemented by all ticket's classes
 */
@Entity( name = "ABSTRACT_TICKET" )
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn( name = "TICKET_TYPE" )
public abstract class Ticket extends EntityWithLongKey {

    private static final long serialVersionUID = 8914735050811003828L;

    /**
     * Cost of the ticket
     */
    @Column( name = "COST" )
    private float cost;

    /**
     * List of travel means the ticket is applicable to
     */
    @OneToMany( fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
    @JoinColumn( name = "RELATED_TO" )
    private List < PublicTravelMean > relatedTo;

    /**
     * List of travel components in which the ticket is selected as used
     */
    @JoinTable( name = "LINKED_PATHS" )
    @OneToMany( fetch = FetchType.LAZY )
    private List < TravelComponent > linkedTravels;

    /**
     * Timestamp in Unix time that memorize the last update of an event
     */
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

    public void setRelatedTo ( ArrayList < PublicTravelMean > relatedTo ) {
        this.relatedTo = relatedTo;
    }

    public void setRelatedTo ( List < PublicTravelMean > relatedTo ) {
        this.relatedTo = relatedTo;
    }

    /**
     * Adds a travel mean into the list of travel means the ticket is applicable to
     *
     * @param travelMean travel mean to be added
     */
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

    /**
     * Adds a travel component in the list of travels for which the user will use this ticket
     *
     * @param travelComponent travel component for which the ticket will be used
     * @throws IncompatibleTravelMeansException if the travel component is not applicable to a ticket since
     *                                          the ticket does not supports that travel mean
     */
    public void addLinkedTravel ( TravelComponent travelComponent )
            throws IncompatibleTravelMeansException {

        if ( isCompatible( travelComponent.getMeanUsed().getType() ) ) {
            throw new IncompatibleTravelMeansException();
        }
        linkedTravels.add( travelComponent );
    }

    /**
     * Checks if a travel mean type is compatible with the ticket
     *
     * @param travelMean travel mean whose compatibility is to be checked
     * @return true if compatible, false otherwise
     */
    public boolean isCompatible ( TravelMeanEnum travelMean ) {
        PublicTravelMean eligibleTravelComponent = relatedTo.stream()
                .filter( publicTravelMean -> publicTravelMean.getType()
                        .equals( travelMean ) )
                .findFirst().orElse( null );

        if ( eligibleTravelComponent == null ) {
            return false;
        }
        return true;
    }

    /**
     * Removes a travel component from the list of travels connected to this ticket, if present
     *
     * @param travelId identifier of the travel component to be removed
     */
    public void removeLinkedTravel ( long travelId ) {
        linkedTravels.removeIf( travelComponent -> travelComponent.getId() == travelId );
    }

    /**
     * Checks if the ticket remains applicable if the specified travel component
     * is added in the list of travelComponents that will take advantage
     * of the ticket
     *
     * @param travelComponentToBeAdded travel component that might be added
     * @throws TicketNotValidException if the ticket is not applicable also to the new travel mean
     */
    public abstract void checkTicketValidityAfterTravelAssociation ( TravelComponent travelComponentToBeAdded )
            throws TicketNotValidException;

    /**
     * Visitor method used to serialize a correct response to the client,
     * useful in order to handle all tickets in the same way
     *
     * @param ticketListResponse message that is to be sent to the client
     */
    public abstract void serializeResponse ( TicketListResponse ticketListResponse );

    /**
     * Visitor method used to serialize a correct response to the client,
     * when a periodTicket must serialize his decorated ticket properly
     *
     * @param periodTicketResponse message that is to be sent to the client
     */
    public abstract void decorateResponse ( PeriodTicketResponse periodTicketResponse );
}
