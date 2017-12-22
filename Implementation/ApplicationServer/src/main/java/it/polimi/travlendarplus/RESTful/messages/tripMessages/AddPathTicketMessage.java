package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages.LocationMessage;
import it.polimi.travlendarplus.beans.tripManager.TripManager;
import it.polimi.travlendarplus.entities.tickets.Ticket;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.IncompatibleTravelMeansException;

import java.util.List;

/**
 * This is the message class sent in the body of an HTTP request to add a
 * path ticket into the user's profile
 */
public class AddPathTicketMessage extends AddGenericTicketMessage {

    private static final long serialVersionUID = 6010171063455341375L;

    private LocationMessage startingLocation;
    private LocationMessage endingLocation;

    public AddPathTicketMessage () {
    }

    public AddPathTicketMessage ( float cost, List < AddPublicTravelMeanMessage > relatedTo,
                                  String lineName, LocationMessage startingLocation,
                                  LocationMessage endingLocation ) {
        super( cost, relatedTo, lineName );
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
    }

    public LocationMessage getStartingLocation () {
        return startingLocation;
    }

    public void setStartingLocation ( LocationMessage startingLocation ) {
        this.startingLocation = startingLocation;
    }

    public LocationMessage getEndingLocation () {
        return endingLocation;
    }

    public void setEndingLocation ( LocationMessage endingLocation ) {
        this.endingLocation = endingLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ticket addTicket ( TripManager tripManager ) throws InvalidFieldException {
        return tripManager.addPathTicket( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ticket modifyTicket ( TripManager tripManager, long ticketId )
            throws InvalidFieldException, EntityNotFoundException, IncompatibleTravelMeansException {
        return tripManager.modifyPathTicket( this, ticketId );
    }
}
