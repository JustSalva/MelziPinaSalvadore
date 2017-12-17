package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.beans.tripManager.TripManager;
import it.polimi.travlendarplus.entities.tickets.Ticket;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.IncompatibleTravelMeansException;

import java.util.ArrayList;
import java.util.List;


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

    public abstract Ticket addTicket ( TripManager tripManager )
            throws InvalidFieldException;

    public abstract Ticket modifyTicket ( TripManager tripManager, long ticketId )
            throws InvalidFieldException, EntityNotFoundException, IncompatibleTravelMeansException;
}
