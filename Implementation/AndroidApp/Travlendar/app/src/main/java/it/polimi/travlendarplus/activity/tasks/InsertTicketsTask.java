package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.ticket.GeneralTicket;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;
import it.polimi.travlendarplus.retrofit.response.ticket.AllTicketsResponse;
import it.polimi.travlendarplus.retrofit.response.ticket.GenericTicketResponse;

public class InsertTicketsTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;
    private AllTicketsResponse tickets;

    public InsertTicketsTask(Context context, AllTicketsResponse tickets) {
        this.database = AppDatabase.getInstance(context);
        this.tickets = tickets;
    }

    protected Void doInBackground(Void... voids) {
        // Add generic tickets.
        for (GenericTicketResponse genericTicket : tickets.getGenericTickets()) {
            // Create ticket.
            Ticket ticket = new Ticket(genericTicket.getId(), genericTicket.getCost());
            ticket.setType(Ticket.TicketType.GENERAL);
            // Create generic ticket.
            GeneralTicket generalTicket = new GeneralTicket(genericTicket.getLineName(), null);
            ticket.setGeneralTicket(generalTicket);
            // Insert the generic event in the DB.
            database.ticketsDao().insert(ticket);
        }
        return null;
    }
}
