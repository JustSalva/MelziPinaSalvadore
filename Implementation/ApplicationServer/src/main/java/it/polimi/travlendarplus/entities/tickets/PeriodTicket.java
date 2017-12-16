package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;

@Entity( name = "PERIOD_TICKET" )
@DiscriminatorValue( "PERIOD" )
public class PeriodTicket extends Ticket {

    private static final long serialVersionUID = -7003608973530344312L;

    @Column( name = "NAME" )
    private String name;

    @Column( name = "STARTING_DATE_OF_CALENDAR" )
    private Instant startingDate;

    @Column( name = "ENDING_DATE_OF_CALENDAR" )
    private Instant endingDate;

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
}
