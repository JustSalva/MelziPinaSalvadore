package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.beans.tripManager.TripManager;
import it.polimi.travlendarplus.entities.tickets.Ticket;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.IncompatibleTravelMeansException;

import java.util.List;

/**
 * This is the message class sent in the body of an HTTP request to add a
 * generic ticket into the user's profile
 */
public class AddGenericTicketMessage extends AddTicketMessage {

    private static final long serialVersionUID = 4485865740788252604L;

    private String lineName;

    public AddGenericTicketMessage () {
    }

    public AddGenericTicketMessage ( float cost, List < AddPublicTravelMeanMessage > relatedTo,
                                     String lineName ) {
        super( cost, relatedTo );
        this.lineName = lineName;
    }

    public String getLineName () {
        return lineName;
    }

    public void setLineName ( String lineName ) {
        this.lineName = lineName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ticket addTicket ( TripManager tripManager ) throws InvalidFieldException {
        return tripManager.addGenericTicket( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ticket modifyTicket ( TripManager tripManager, long ticketId )
            throws InvalidFieldException, EntityNotFoundException, IncompatibleTravelMeansException {

        return tripManager.modifyGenericTicket( this, ticketId );
    }

}
