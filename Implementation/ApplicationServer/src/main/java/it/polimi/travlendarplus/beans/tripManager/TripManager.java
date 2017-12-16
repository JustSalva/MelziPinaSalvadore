package it.polimi.travlendarplus.beans.tripManager;

import it.polimi.travlendarplus.RESTful.messages.tripMessages.*;
import it.polimi.travlendarplus.beans.calendarManager.UserManager;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.tickets.*;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class TripManager  extends UserManager {

    public List< Ticket > getTickets () {
        return currentUser.getHeldTickets();
    }

    public DistanceTicket addDistanceTicket ( AddDistanceTicketMessage distanceTicketMessage )
            throws InvalidFieldException {
        checkDistanceTicketConsistency( distanceTicketMessage );
        DistanceTicket distanceTicket = createDistanceTicket( distanceTicketMessage );
        distanceTicket.save();
        currentUser.addTicket( distanceTicket );
        currentUser.save();
        return distanceTicket;
    }

    private void checkTicketConsistency( AddTicketMessage addTicketMessage)
            throws InvalidFieldException {
        //TODO
    }
    private void checkDistanceTicketConsistency( AddDistanceTicketMessage distanceTicketMessage)
            throws InvalidFieldException {
        checkTicketConsistency( distanceTicketMessage );
        //TODO
    }

    private DistanceTicket createDistanceTicket( AddDistanceTicketMessage distanceTicketMessage )
            throws InvalidFieldException {
        //TODO
        return null;
    }

    public GenericTicket addGenericTicket ( AddGenericTicketMessage genericTicketMessage )
            throws InvalidFieldException {
        return null;
    }

    public PathTicket addPathTicket ( AddPathTicketMessage pathTicketMessage )
            throws InvalidFieldException {
        return null;
    }

    public PeriodTicket addPeriodTicket ( AddPeriodTicketMessage periodTicketMessage )
            throws InvalidFieldException {
        return null;
    }

    public void deleteTicket ( long ticketId ) throws EntityNotFoundException {
        //TODO
    }

    public void selectTicket( long ticketId, long travelComponentId ) throws EntityNotFoundException {
        //TODO
    }
    public void deselectTicket( long ticketId, long travelComponentId ) throws EntityNotFoundException {
        //TODO
    }

}
