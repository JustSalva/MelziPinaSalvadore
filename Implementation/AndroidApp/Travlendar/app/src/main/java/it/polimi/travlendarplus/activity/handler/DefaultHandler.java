package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import it.polimi.travlendarplus.activity.LoginActivity;
import it.polimi.travlendarplus.activity.tasks.RemoveUserTask;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;

/**
 * Default handler to be implemented by all the other handlers.
 * Handles basic error messages.
 */
public abstract class DefaultHandler extends Handler {

    public Context context;

    public DefaultHandler(Looper looper, Context context) {
        super(looper);
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 0:
                Toast.makeText(context, "No internet connection available!", Toast.LENGTH_LONG).show();
                break;
            case 400:
                // Shows the user which invalid fields have been sent to server.
                ErrorResponse errorResponse = new Gson()
                        .fromJson(msg.getData().getString("errorResponse"), ErrorResponse.class);
                // Shows a toast for each error message.
                if (errorResponse.getMessages() == null) {
                    Toast.makeText(context, "Invalid request sent to server!", Toast.LENGTH_LONG).show();
                } else {
                    for (String message : errorResponse.getMessages()) {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case 401:
                Toast.makeText(context, "You logged in from another device!", Toast.LENGTH_LONG).show();
                new RemoveUserTask(context).execute();
                context.startActivity(new Intent(context, LoginActivity.class));
                break;
            default:
                Log.d("UNKNOWN_ERROR", msg.toString());
                break;
        }
    }
}
