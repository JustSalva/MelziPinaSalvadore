package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.beans.tripManager.TripManager;
import it.polimi.travlendarplus.entities.tickets.Ticket;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.IncompatibleTravelMeansException;

import java.util.List;

/**
 * This is the message class sent in the body of an HTTP request to add a
 * distance ticket into the user's profile
 */
public class AddDistanceTicketMessage extends AddTicketMessage {

    private static final long serialVersionUID = 9180454774057114189L;

    private int distance;

    public AddDistanceTicketMessage () {
    }

    public AddDistanceTicketMessage ( float cost, List < AddPublicTravelMeanMessage > relatedTo, int distance ) {
        super( cost, relatedTo );
        this.distance = distance;
    }

    public int getDistance () {
        return distance;
    }

    public void setDistance ( int distance ) {
        this.distance = distance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ticket addTicket ( TripManager tripManager ) throws InvalidFieldException {
        return tripManager.addDistanceTicket( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ticket modifyTicket ( TripManager tripManager, long ticketId )
            throws InvalidFieldException, EntityNotFoundException, IncompatibleTravelMeansException {
        return tripManager.modifyDistanceTicket( this, ticketId );
    }
}
