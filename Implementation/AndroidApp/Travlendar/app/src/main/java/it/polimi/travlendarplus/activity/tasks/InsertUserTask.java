package it.polimi.travlendarplus.activity.tasks;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.User;

/**
 * Task that inserts a list of users in the DB.
 */
public class InsertUserTask extends AsyncTask<User, Void, Void> {

    private Activity context;
    private AppDatabase database;

    public InsertUserTask(Activity context) {
        this.context = context;
        this.database = AppDatabase.getInstance(context);
    }

    @Override
    protected Void doInBackground(User... users) {
        for (User user : users) {
            database.userDao().delete();
            database.userDao().insert(user);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        context.runOnUiThread(() -> context.startActivity(new Intent(context, CalendarActivity.class)));
    }
}
