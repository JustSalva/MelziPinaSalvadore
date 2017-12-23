package it.polimi.travlendarplus.activity.tasks;


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

    private Context context;
    private AppDatabase database;

    public InsertUserTask(Context context) {
        this.context = context;
        this.database = AppDatabase.getInstance(context);
    }

    protected Void doInBackground(User... users) {
        for (User user : users) {
            database.userDao().delete();
            database.userDao().insert(user);
        }
        context.startActivity(new Intent(context, CalendarActivity.class));
        return null;
    }
}
