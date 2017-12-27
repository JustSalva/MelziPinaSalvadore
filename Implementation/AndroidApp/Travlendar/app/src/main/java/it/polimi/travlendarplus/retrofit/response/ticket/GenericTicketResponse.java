package it.polimi.travlendarplus.retrofit.response.ticket;


public class GenericTicketResponse extends TicketResponse {

    private String lineName;

    public String getLineName() {
        return lineName;
    }

    @Override
    public String toString() {
        return super.toString().concat(" LineName("+lineName+") ");
    }
}
