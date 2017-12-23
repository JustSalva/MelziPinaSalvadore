package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.entities.tickets.DistanceTicket;
import it.polimi.travlendarplus.entities.tickets.GenericTicket;
import it.polimi.travlendarplus.entities.tickets.PathTicket;
import it.polimi.travlendarplus.entities.tickets.Ticket;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a message class sent to reply with all needed info
 * to a getTickets request with a proper and readable response message
 */
public class TicketListResponse extends TripResponse {

    private static final long serialVersionUID = 8848546547229766767L;

    private List < DistanceTicket > distanceTickets;
    private List < GenericTicket > genericTickets;
    private List < PathTicket > pathTickets;
    private List < PeriodTicketResponse > periodTickets;

    public TicketListResponse () {
    }

    public TicketListResponse ( List < Ticket > userTickets ) {
        this.distanceTickets = new ArrayList <>();
        this.genericTickets = new ArrayList <>();
        this.pathTickets = new ArrayList <>();
        this.periodTickets = new ArrayList <>();
        for ( Ticket ticket : userTickets ) {
            ticket.serializeResponse( this );
        }
    }

    public List < DistanceTicket > getDistanceTickets () {
        return distanceTickets;
    }

    public void setDistanceTickets ( List < DistanceTicket > distanceTickets ) {
        this.distanceTickets = distanceTickets;
    }

    public void addDistanceTicket ( DistanceTicket distanceTicket ) {
        this.distanceTickets.add( distanceTicket );
    }

    public List < GenericTicket > getGenericTickets () {
        return genericTickets;
    }

    public void setGenericTickets ( List < GenericTicket > genericTickets ) {
        this.genericTickets = genericTickets;
    }

    public void addGenericTicket ( GenericTicket genericTicket ) {
        this.genericTickets.add( genericTicket );
    }

    public List < PathTicket > getPathTickets () {
        return pathTickets;
    }

    public void setPathTickets ( List < PathTicket > pathTickets ) {
        this.pathTickets = pathTickets;
    }

    public void addPathTicket ( PathTicket pathTicket ) {
        this.pathTickets.add( pathTicket );
    }


    public List < PeriodTicketResponse > getPeriodTickets () {
        return periodTickets;
    }

    public void setPeriodTickets ( List < PeriodTicketResponse > periodTickets ) {
        this.periodTickets = periodTickets;
    }

    public void addPeriodTicket ( PeriodTicketResponse periodTicket ) {
        this.periodTickets.add( periodTicket );
    }
}
