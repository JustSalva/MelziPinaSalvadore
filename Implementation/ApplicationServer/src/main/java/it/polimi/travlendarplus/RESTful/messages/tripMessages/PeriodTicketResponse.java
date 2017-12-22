package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.entities.tickets.DistanceTicket;
import it.polimi.travlendarplus.entities.tickets.GenericTicket;
import it.polimi.travlendarplus.entities.tickets.PathTicket;
import it.polimi.travlendarplus.entities.tickets.PeriodTicket;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PeriodTicketResponse extends TripResponse {

    private static final long serialVersionUID = 7271436663136619574L;

    private String name;
    private Instant startingDate;
    private Instant endingDate;
    private List< TravelComponent > linkedTravels;

    private DistanceTicket distanceTicket;
    private GenericTicket genericTicket;
    private PathTicket pathTicket;

    public PeriodTicketResponse () {
    }

    public PeriodTicketResponse( PeriodTicket periodTicket){
        periodTicket.decorateResponse( this );
        this.name = periodTicket.getName();
        this.startingDate = periodTicket.getStartingDate();
        this.endingDate = periodTicket.getEndingDate();
        this.linkedTravels = new ArrayList <>( periodTicket.getLinkedTravels() );
    }

    public DistanceTicket getDistanceTicket () {
        return distanceTicket;
    }

    public void setDistanceTicket ( DistanceTicket distanceTicket ) {
        this.distanceTicket = distanceTicket;
    }

    public GenericTicket getGenericTicket () {
        return genericTicket;
    }

    public void setGenericTicket ( GenericTicket genericTicket ) {
        this.genericTicket = genericTicket;
    }

    public PathTicket getPathTicket () {
        return pathTicket;
    }

    public void setPathTicket ( PathTicket pathTicket ) {
        this.pathTicket = pathTicket;
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

    public List < TravelComponent > getLinkedTravels () {
        return linkedTravels;
    }

    public void setLinkedTravels ( List < TravelComponent > linkedTravels ) {
        this.linkedTravels = linkedTravels;
    }
}
