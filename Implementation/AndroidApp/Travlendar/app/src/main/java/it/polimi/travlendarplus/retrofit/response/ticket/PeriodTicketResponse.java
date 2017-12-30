package it.polimi.travlendarplus.retrofit.response.ticket;


import it.polimi.travlendarplus.Timestamp;

public class PeriodTicketResponse extends TicketResponse {

    private String name;
    private Timestamp startingDate;
    private Timestamp endingDate;

    private GenericTicketResponse genericTicket;
    private DistanceTicketResponse distanceTicket;
    private PathTicketResponse pathTicket;

    public String getName () {
        return name;
    }

    public Timestamp getStartingDate () {
        return startingDate;
    }

    public Timestamp getEndingDate () {
        return endingDate;
    }

    public GenericTicketResponse getGenericTicket () {
        return genericTicket;
    }

    public DistanceTicketResponse getDistanceTicket () {
        return distanceTicket;
    }

    public PathTicketResponse getPathTicket () {
        return pathTicket;
    }

    @Override
    public String toString () {
        return "PERIOD TICKET \n" +
                super.toString() +
                "Name(" + name + ")";
    }
}
