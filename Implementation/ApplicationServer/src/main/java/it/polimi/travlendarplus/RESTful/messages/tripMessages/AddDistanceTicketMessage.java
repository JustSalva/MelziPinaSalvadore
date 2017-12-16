package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.beans.tripManager.TripManager;
import it.polimi.travlendarplus.entities.tickets.Ticket;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class AddDistanceTicketMessage extends AddTicketMessage {

    private static final long serialVersionUID = 9180454774057114189L;

    private int distance;

    @JsonCreator
    public AddDistanceTicketMessage ( @JsonProperty( "cost" ) float cost,
                                      @JsonProperty( "relatedTo" ) List < AddPublicTravelMeanMessage > relatedTo,
                                      @JsonProperty( "distance" ) int distance ) {
        super( cost, relatedTo );
        this.distance = distance;
    }

    public AddDistanceTicketMessage () {
    }

    public int getDistance () {
        return distance;
    }

    public void setDistance ( int distance ) {
        this.distance = distance;
    }

    @Override
    public Ticket addTicket ( TripManager tripManager ) throws InvalidFieldException {
        return tripManager.addDistanceTicket( this );
    }
}
