package it.polimi.travlendarplus.retrofit.response.ticket;


public class GenericTicketResponse extends TicketResponse {

    private String lineName;

    public String getLineName () {
        return lineName;
    }

    @Override
    public String toString () {
        return "GENERIC TICKET\n" +
                super.toString() +
                " LineName(" + lineName + ")\n";
    }
}
