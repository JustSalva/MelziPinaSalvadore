package it.polimi.travlendarplus.activity.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.EventEditorActivity;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;


/**
 * Handler that handles the server response to the event addition.
 * It is used by the EventEditorActivity.
 */
public class AddEventHandler extends Handler {

    private Context context;
    private EventEditorActivity eventEditorActivity;

    public AddEventHandler(Looper looper, Context context, EventEditorActivity eventEditorActivity) {
        super(looper);
        this.context = context;
        this.eventEditorActivity = eventEditorActivity;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 0:
                Toast.makeText(context, "No internet connection available!", Toast.LENGTH_LONG).show();
                break;
            case 200:
                // Notify the user that the event has been added.
                Toast.makeText(context, "Event added!", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, CalendarActivity.class));
                break;
            case 400:
                // Shows the user which invalid fields have been sent to server.
                Toast.makeText(context, "Invalid fields sent to server!", Toast.LENGTH_LONG).show();
                ErrorResponse errorResponse = new Gson()
                        .fromJson(msg.getData().getString("errorResponse"), ErrorResponse.class);
                // Shows a toast for each error message.
                for (String message : errorResponse.getMessages()) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
                break;
            case 503:
                Toast.makeText(context, "Google Maps not reachable!", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                break;
        }
        eventEditorActivity.resumeNormalMode();
    }
}
