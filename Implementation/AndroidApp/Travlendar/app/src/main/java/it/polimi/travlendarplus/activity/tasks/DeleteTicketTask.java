package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;

public class DeleteTicketTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;
    private int eventId;

    public DeleteTicketTask(Context context, int eventId) {
        this.database = AppDatabase.getInstance(context);
        this.eventId = eventId;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        database.ticketsDao().deleteFromId(eventId);
        return null;
    }
}
