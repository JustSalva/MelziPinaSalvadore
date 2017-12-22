package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.beans.tripManager.TripManager;
import it.polimi.travlendarplus.entities.tickets.Ticket;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.IncompatibleTravelMeansException;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a generic message class sent in the body of an HTTP request to add a
 * ticket into the user's profile, it is to be extended by all ticket messages
 */
public abstract class AddTicketMessage extends TripMessage {

    private static final long serialVersionUID = -2283665671474633230L;

    private float cost;

    private List < AddPublicTravelMeanMessage > relatedTo;

    public AddTicketMessage () {
        relatedTo = new ArrayList <>();
    }

    public AddTicketMessage ( float cost, List < AddPublicTravelMeanMessage > relatedTo ) {
        this.cost = cost;
        this.relatedTo = relatedTo;
    }

    public float getCost () {
        return cost;
    }

    public void setCost ( float cost ) {
        this.cost = cost;
    }

    public List < AddPublicTravelMeanMessage > getRelatedTo () {
        return relatedTo;
    }

    public void setRelatedTo ( List < AddPublicTravelMeanMessage > relatedTo ) {
        this.relatedTo = relatedTo;
    }

    /**
     * Visitor method used to handle the addition of a ticket in the user's profile
     * without knowing his specific type but only that is a ticket message
     *
     * @param tripManager manager that will handle the ticket addition
     * @return the added distance ticket info
     * @throws InvalidFieldException if some fields of the message are wrong
     *                               ( which one is specified inside the exception)
     */
    public abstract Ticket addTicket ( TripManager tripManager )
            throws InvalidFieldException;

    /**
     * Visitor method used to modify a ticket in the user's profile
     * without knowing his specific type but only that is a ticket message
     *
     * @param tripManager manager that will handle the ticket addition
     * @param ticketId    identifier of the ticket to be modified
     * @return the modified distance ticket info
     * @throws InvalidFieldException            if some fields of the message are wrong
     *                                          ( which one is specified inside the exception)
     * @throws EntityNotFoundException          if the specified ticket to be modified doesn't exist
     * @throws IncompatibleTravelMeansException if after the update one or more public travel
     *                                          means would become incompatible
     */
    public abstract Ticket modifyTicket ( TripManager tripManager, long ticketId )
            throws InvalidFieldException, EntityNotFoundException, IncompatibleTravelMeansException;
}
