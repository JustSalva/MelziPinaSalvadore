package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;

public class DeleteTicketTask extends AsyncTask < Void, Void, Void > {

    private AppDatabase database;
    private int ticketId;

    public DeleteTicketTask ( Context context, int ticketId ) {
        this.database = AppDatabase.getInstance( context );
        this.ticketId = ticketId;
    }

    @Override
    protected Void doInBackground ( Void... voids ) {
        // Delete ticket from tickets table.
        database.ticketsDao().deleteFromId( ticketId );
        // Delete ticket from travel component table.
        database.ticketsDao().removeTicketFromTravelComponent( ticketId );
        return null;
    }
}
