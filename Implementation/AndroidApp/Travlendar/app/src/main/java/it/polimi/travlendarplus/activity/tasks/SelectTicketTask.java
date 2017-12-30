package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;

public class SelectTicketTask extends AsyncTask < Void, Void, Void > {

    private AppDatabase database;
    private boolean select;
    private int ticketId;
    private int travelComponentId;

    public SelectTicketTask ( Context context, boolean select, int ticketId, int travelComponentId ) {
        this.database = AppDatabase.getInstance( context );
        this.select = select;
        this.ticketId = ticketId;
        this.travelComponentId = travelComponentId;
    }

    protected Void doInBackground ( Void... voids ) {
        if ( select ) {
            // Add ticket to travel component.
            database.calendarDao().selectTicket( ticketId, travelComponentId );
        } else {
            // Remove ticket from travel component.
            database.calendarDao().deselectTicket( travelComponentId );
        }
        return null;
    }
}
