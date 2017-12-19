package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;

/**
 * This JPA class represent a ticket whose validity is related to the travel length
 */
@Entity( name = "DISTANCE_TICKET" )
@DiscriminatorValue( "DISTANCE" )
public class DistanceTicket extends Ticket {

    private static final long serialVersionUID = 2628317705098639359L;

    /**
     * Maximum travel distance allowed usin this ticket
     */
    @Column( name = "DISTANCE" )
    private int distance;

    public DistanceTicket () {
    }

    public DistanceTicket ( float cost, ArrayList < PublicTravelMean > relatedTo, int distance ) {
        super( cost, relatedTo );
        this.distance = distance;
    }

    /**
     * Allows to load a DistanceTicket class from the database
     *
     * @param key primary key of the distanceTicket tuple
     * @return the requested tuple as a DistanceTicket class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static DistanceTicket load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( DistanceTicket.class, key );
    }

    public int getDistance () {
        return distance;
    }

    public void setDistance ( int distance ) {
        this.distance = distance;
    }
}
