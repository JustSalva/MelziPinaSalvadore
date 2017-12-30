package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;


/**
 * Task that deletes an event specified by the parameter eventId from the DB.
 */
public class DeleteEventTask extends AsyncTask < Void, Void, Void > {

    private AppDatabase database;
    private int eventId;

    public DeleteEventTask ( Context context, int eventId ) {
        this.database = AppDatabase.getInstance( context );
        this.eventId = eventId;
    }

    protected Void doInBackground ( Void... voids ) {
        // Delete event from the DB.
        database.calendarDao().deleteEventFromId( eventId );
        return null;
    }
}
