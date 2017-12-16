package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.beans.tripManager.TripManager;
import it.polimi.travlendarplus.entities.tickets.Ticket;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;

import java.time.Instant;
import java.util.List;

public class AddPeriodTicketMessage extends AddTicketMessage {

    private static final long serialVersionUID = 4030989447613137135L;

    private String name;
    private Instant startingDate;
    private Instant endingDate;
    private AddTicketMessage decorator;

    public AddPeriodTicketMessage () {
    }

    public AddPeriodTicketMessage ( float cost, List < AddPublicTravelMeanMessage > relatedTo, String name,
                                    Instant startingDate, Instant endingDate, AddTicketMessage decorator ) {
        super( cost, relatedTo );
        this.name = name;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.decorator = decorator;
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

    public AddTicketMessage getDecorator () {
        return decorator;
    }

    public void setDecorator ( AddTicketMessage decorator ) {
        this.decorator = decorator;
    }

    @Override
    public Ticket addTicket ( TripManager tripManager ) throws InvalidFieldException {
        return tripManager.addPeriodTicket( this );
    }
}
