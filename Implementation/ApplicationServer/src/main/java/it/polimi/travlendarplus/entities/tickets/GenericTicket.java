package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

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
}
