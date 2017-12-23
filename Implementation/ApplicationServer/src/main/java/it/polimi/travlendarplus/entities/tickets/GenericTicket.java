package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.RESTful.messages.tripMessages.PeriodTicketResponse;
import it.polimi.travlendarplus.RESTful.messages.tripMessages.TicketListResponse;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.TicketNotValidException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;

/**
 * This JPA class represent a ticket whose validity is related to a specific public transportation route
 */
@Entity( name = "GENERIC_TICKET" )
@DiscriminatorValue( "GENERIC" )
public class GenericTicket extends Ticket {

    private static final long serialVersionUID = -8507655590498704818L;

    /**
     * Name of the route the ticket is associated to
     */
    @Column( name = "LINE_NAME" )
    private String lineName;

    public GenericTicket () {
    }

    public GenericTicket ( float cost, ArrayList < PublicTravelMean > relatedTo, String lineName ) {
        super( cost, relatedTo );
        this.lineName = lineName;
    }

    /**
     * Allows to load a GenericTicket class from the database
     *
     * @param key primary key of the genericTicket tuple
     * @return the requested tuple as a GenericTicket class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static GenericTicket load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( GenericTicket.class, key );
    }

    public String getLineName () {
        return lineName;
    }

    public void setLineName ( String lineName ) {
        this.lineName = lineName;
    }

    /**
     * {@inheritDoc}
     *
     * @param travelComponentToBeAdded {@inheritDoc}
     * @throws TicketNotValidException if the travel component to be added belongs
     *                                 to a different public transportation line
     */
    @Override
    public void checkTicketValidityAfterTravelAssociation ( TravelComponent travelComponentToBeAdded )
            throws TicketNotValidException {
        //TODO line names not yet handled
        //throw new TicketNotValidException( TicketNotValidCauses.WRONG_LINE_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serializeResponse ( TicketListResponse ticketListResponse ) {
        ticketListResponse.addGenericTicket( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decorateResponse ( PeriodTicketResponse periodTicketResponse ) {
        periodTicketResponse.setGenericTicket( this );
    }
}
