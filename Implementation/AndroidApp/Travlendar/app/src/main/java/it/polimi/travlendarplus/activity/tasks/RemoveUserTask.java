package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;

/**
 * Performs an User input operation in the DB on a separated thread.
 */
public class RemoveUserTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;

    public RemoveUserTask(Context context) {
        this.database = AppDatabase.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        database.userDao().delete();
        return null;
    }
}
