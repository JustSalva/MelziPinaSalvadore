package it.polimi.travlendarplus.retrofit.response.ticket;


import java.util.List;

public class AllTicketsResponse {

    private List<DistanceTicketResponse> distanceTickets;
    private List<GenericTicketResponse> genericTickets;
    private List<PathTicketResponse> pathTickets;
    private List<PeriodTicketResponse> periodTickets;

    public List<DistanceTicketResponse> getDistanceTickets() {
        return distanceTickets;
    }

    public List<GenericTicketResponse> getGenericTickets() {
        return genericTickets;
    }

    public List<PathTicketResponse> getPathTickets() {
        return pathTickets;
    }

    public List<PeriodTicketResponse> getPeriodTickets() {
        return periodTickets;
    }
}
