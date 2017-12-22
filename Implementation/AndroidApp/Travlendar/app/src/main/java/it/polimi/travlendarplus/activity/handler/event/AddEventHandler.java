package it.polimi.travlendarplus.activity.handler.event;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.EventEditorActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;


/**
 * Handler that handles the server response to the event addition.
 * It is used by the EventEditorActivity.
 */
public class AddEventHandler extends DefaultHandler {

    private EventEditorActivity eventEditorActivity;

    public AddEventHandler(Looper looper, Context context, EventEditorActivity eventEditorActivity) {
        super(looper, context);
        this.eventEditorActivity = eventEditorActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                // Notify the user that the event has been added.
                Toast.makeText(context, "Event added!", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, CalendarActivity.class));
                break;
            case 503:
                Toast.makeText(context, "Google Maps not reachable!", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        eventEditorActivity.resumeNormalMode();
    }
}
