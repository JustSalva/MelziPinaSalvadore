package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;

public class DeleteEventTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;
    private int eventId;

    public DeleteEventTask(Context context, int eventId) {
        this.database = AppDatabase.getInstance(context);
        this.eventId = eventId;
    }

    protected Void doInBackground(Void... voids) {
        // Update timestamp in the user table of the DB.
        database.calendarDao().deleteFromId(eventId);
        return null;
    }
}
