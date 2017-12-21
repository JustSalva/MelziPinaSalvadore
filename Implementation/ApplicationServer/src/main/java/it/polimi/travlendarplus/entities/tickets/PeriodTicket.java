package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.TicketNotValidCauses;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.TicketNotValidException;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * This JPA class represent a ticket whose validity is related to a period of time
 * ( es. monthly ). It decorate any other type of ticket,
 * in order to represent their validity in time
 */
@Entity( name = "PERIOD_TICKET" )
@DiscriminatorValue( "PERIOD" )
public class PeriodTicket extends Ticket {

    private static final long serialVersionUID = -7003608973530344312L;

    /**
     * Name of the periodic ticket
     */
    @Column( name = "NAME" )
    private String name;

    /**
     * Instant when the ticket validity starts
     */
    @Column( name = "STARTING_DATE_OF_CALENDAR" )
    private Instant startingDate;

    /**
     * Instant when the ticket validity ends
     */
    @Column( name = "ENDING_DATE_OF_CALENDAR" )
    private Instant endingDate;

    /**
     * Ticket whose validity is extended in time
     */
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @JoinColumn( name = "PERIODICAL_TICKET" )
    private Ticket decorator;

    public PeriodTicket () {
    }

    public PeriodTicket ( float cost, ArrayList < PublicTravelMean > relatedTo, String name,
                          Instant startingDate, Instant endingDate, Ticket decorator ) {
        super( cost, relatedTo );
        this.name = name;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.decorator = decorator;
    }

    /**
     * Allows to load a PeriodTicket class from the database
     *
     * @param key primary key of the periodTicket tuple
     * @return the requested tuple as a PeriodTicket class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static PeriodTicket load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( PeriodTicket.class, key );
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public Instant getStartingDate () {
        return startingDate;
    }

    public void setStartingDate ( Instant startingDate ) {
        this.startingDate = startingDate;
    }

    public Instant getEndingDate () {
        return endingDate;
    }

    public void setEndingDate ( Instant endingDate ) {
        this.endingDate = endingDate;
    }

    public Ticket getDecorator () {
        return decorator;
    }

    public void setDecorator ( Ticket decorator ) {
        this.decorator = decorator;
    }

    /**
     * {@inheritDoc}
     *
     * @param travelComponentToBeAdded {@inheritDoc}
     * @throws TicketNotValidException if the travel component exceed the period
     *                                 of validity of the ticket
     */
    @Override
    public void checkTicketValidityAfterTravelAssociation ( TravelComponent travelComponentToBeAdded )
            throws TicketNotValidException {
        List < String > conflicts = new ArrayList <>();
        try {
            decorator.checkTicketValidityAfterTravelAssociation( travelComponentToBeAdded );
        } catch ( TicketNotValidException e ) {
            conflicts.addAll( e.getErrors() );
        }
        if ( travelComponentToBeAdded.getStartingTime().isBefore( this.startingDate ) ||
                travelComponentToBeAdded.getEndingTime().isAfter( this.endingDate ) ) {
            TicketNotValidException ticketNotValidException =
                    new TicketNotValidException( TicketNotValidCauses.OUT_OF_VALIDITY_PERIOD );
            ticketNotValidException.addErrors( conflicts );
        }
    }
}
