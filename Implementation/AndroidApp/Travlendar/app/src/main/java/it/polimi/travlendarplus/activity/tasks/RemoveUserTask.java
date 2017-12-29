package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;

/**
 * Task that deletes a user contained in the DB.
 */
public class RemoveUserTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;

    public RemoveUserTask(Context context) {
        this.database = AppDatabase.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        database.userDao().delete();
        database.calendarDao().deleteAllTravelComponents();
        database.calendarDao().deleteAllGenericEvents();
        database.ticketsDao().deleteAll();
        return null;
    }
}
