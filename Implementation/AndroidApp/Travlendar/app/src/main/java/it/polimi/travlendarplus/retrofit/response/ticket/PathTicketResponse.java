package it.polimi.travlendarplus.retrofit.response.ticket;


import it.polimi.travlendarplus.Position;

public class PathTicketResponse extends GenericTicketResponse {

    private Position startingLocation;
    private Position endingLocation;

    public Position getStartingLocation () {
        return startingLocation;
    }

    public Position getEndingLocation () {
        return endingLocation;
    }

    @Override
    public String toString () {
        return "PATH TICKET \n" +
                super.toString() +
                startingLocation.getAddress() + " to " + endingLocation.getAddress();
    }
}
