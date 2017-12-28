package it.polimi.travlendarplus.retrofit.response.ticket;


public class DistanceTicketResponse extends TicketResponse {

    private int distance;

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "DISTANCE TICKET \n" +
                super.toString() +
                Integer.toString(distance);
    }
}
