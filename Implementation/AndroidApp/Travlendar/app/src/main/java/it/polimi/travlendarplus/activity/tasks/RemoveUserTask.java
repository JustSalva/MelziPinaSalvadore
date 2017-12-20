package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;

/**
 * Clears the DB when an user logs out.
 */
public class RemoveUserTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;

    public RemoveUserTask(Context context) {
        this.database = AppDatabase.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        database.userDao().delete();
        database.calendarDao().deleteTravelComponents();
        database.calendarDao().deleteAll();
        database.ticketsDao().deleteAll();
        return null;
    }
}
