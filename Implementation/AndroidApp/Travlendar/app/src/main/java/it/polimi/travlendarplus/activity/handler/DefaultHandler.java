package it.polimi.travlendarplus.activity.handler;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.LoginActivity;
import it.polimi.travlendarplus.activity.MainActivity;
import it.polimi.travlendarplus.activity.tasks.RemoveUserTask;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;

/**
 * Default handler to be implemented by all the other handlers.
 * Handles basic error messages.
 */
public abstract class DefaultHandler<A extends Activity> extends Handler {

    public A activity;

    public DefaultHandler(Looper looper, A activity) {
        super(looper);
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 0:
                Toast.makeText(activity, "No internet connection available!", Toast.LENGTH_LONG).show();
                //context.startActivity(new Intent(context, MainActivity.class));
                break;
            case 400:
                // Shows the user which invalid fields have been sent to server.
                ErrorResponse errorResponse = new Gson()
                        .fromJson(msg.getData().getString("errorResponse"), ErrorResponse.class);
                // Shows a toast for each error message.
                if (errorResponse == null || errorResponse.getMessages() == null) {
                    Toast.makeText(activity, "Invalid request sent to server!", Toast.LENGTH_LONG).show();
                } else {
                    for (String message : errorResponse.getMessages()) {
                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case 401:
                Toast.makeText(activity, "You logged in from another device!", Toast.LENGTH_LONG).show();
                new RemoveUserTask(activity.getApplicationContext()).execute();
                activity.startActivity(new Intent(activity, LoginActivity.class));
                break;
            default:
                Log.d("UNKNOWN_ERROR", msg.toString());
                break;
        }
    }
}
