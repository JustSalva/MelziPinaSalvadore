package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.ticket.DistanceTicket;
import it.polimi.travlendarplus.database.entity.ticket.GenericTicket;
import it.polimi.travlendarplus.database.entity.ticket.PathTicket;
import it.polimi.travlendarplus.database.entity.ticket.PeriodTicket;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;
import it.polimi.travlendarplus.retrofit.response.ticket.AllTicketsResponse;
import it.polimi.travlendarplus.retrofit.response.ticket.DistanceTicketResponse;
import it.polimi.travlendarplus.retrofit.response.ticket.GenericTicketResponse;
import it.polimi.travlendarplus.retrofit.response.ticket.PathTicketResponse;
import it.polimi.travlendarplus.retrofit.response.ticket.PeriodTicketResponse;

public class InsertTicketsTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;
    private AllTicketsResponse tickets;

    public InsertTicketsTask(Context context, AllTicketsResponse tickets) {
        this.database = AppDatabase.getInstance(context);
        this.tickets = tickets;
    }

    protected Void doInBackground(Void... voids) {
        // Add generic tickets.
        for (GenericTicketResponse ticketResponse : tickets.getGenericTickets()) {
            // Create ticket.
            Ticket ticket = new Ticket(ticketResponse.getId(), ticketResponse.getCost());
            ticket.setType(Ticket.TicketType.GENERIC);
            // Create generic ticket.
            GenericTicket genericTicket = new GenericTicket(ticketResponse.getLineName(), null);
            ticket.setGenericTicket(genericTicket);
            // Insert the generic ticket in the DB.
            database.ticketsDao().insert(ticket);
        }
        // Add distance ticket.
        for (DistanceTicketResponse ticketResponse : tickets.getDistanceTickets()) {
            // Create ticket.
            Ticket ticket = new Ticket(ticketResponse.getId(), ticketResponse.getCost());
            ticket.setType(Ticket.TicketType.DISTANCE);
            // Create distance ticket.
            DistanceTicket distanceTicket = new DistanceTicket(ticketResponse.getDistance());
            ticket.setDistanceTicket(distanceTicket);
            // Insert the distance ticket in the DB.
            database.ticketsDao().insert(ticket);
        }
        // Add path tickets.
        for (PathTicketResponse ticketResponse : tickets.getPathTickets()) {
            // Create ticket.
            Ticket ticket = new Ticket(ticketResponse.getId(), ticketResponse.getCost());
            ticket.setType(Ticket.TicketType.PATH);
            // Create path ticket.
            PathTicket pathTicket = new PathTicket(
                    ticketResponse.getStartingLocation().getAddress(),
                    ticketResponse.getEndingLocation().getAddress()
            );
            // Create generic ticket.
            GenericTicket genericTicket = new GenericTicket(ticketResponse.getLineName(), pathTicket);
            ticket.setGenericTicket(genericTicket);
            // Insert the path ticket in the DB.
            database.ticketsDao().insert(ticket);
        }
        // Add period ticket.
        for (PeriodTicketResponse ticketResponse : tickets.getPeriodTickets()) {
            // Create ticket.
            Ticket ticket = new Ticket(ticketResponse.getId(), ticketResponse.getCost());
            ticket.setType(Ticket.TicketType.PERIOD);
            // Create period ticket.
            PeriodTicket periodTicket = new PeriodTicket(
                    ticketResponse.getName(),
                    ticketResponse.getStartingDate().getSeconds(),
                    ticketResponse.getEndingDate().getSeconds()
            );
            ticket.setPeriodTicket(periodTicket);
            // Set decorator.
            if (ticketResponse.getDistanceTicket() != null) {
                periodTicket.setDecoratorType(Ticket.TicketType.DISTANCE);
                ticket.setDistanceTicket(new DistanceTicket(ticketResponse.getDistanceTicket().getDistance()));
            } else if (ticketResponse.getGenericTicket() != null){
                periodTicket.setDecoratorType(Ticket.TicketType.GENERIC);
                ticket.setGenericTicket(new GenericTicket(
                        ticketResponse.getGenericTicket().getLineName(),
                        null
                ));
            } else if (ticketResponse.getPathTicket() != null) {
                periodTicket.setDecoratorType(Ticket.TicketType.PATH);
                ticket.setGenericTicket(new GenericTicket(
                        ticketResponse.getGenericTicket().getLineName(),
                        new PathTicket(
                                ticketResponse.getPathTicket().getStartingLocation().getAddress(),
                                ticketResponse.getPathTicket().getEndingLocation().getAddress()
                        )
                ));
            }
            // Insert the path ticket in the DB.
            database.ticketsDao().insert(ticket);
        }
        return null;
    }
}
