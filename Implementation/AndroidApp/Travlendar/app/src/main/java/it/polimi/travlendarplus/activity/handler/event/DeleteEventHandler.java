package it.polimi.travlendarplus.activity.handler.event;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.activity.tasks.DeleteEventTask;

/**
 * Handler that handles the server response to the event deletion.
 * It is used by the CalendarActivity.
 */
public class DeleteEventHandler extends DefaultHandler {

    private CalendarActivity calendarActivity;

    public DeleteEventHandler(Looper looper, Context context, CalendarActivity calendarActivity) {
        super(looper, context);
        this.calendarActivity = calendarActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                int eventId = msg.getData().getInt("Id");
                // Notify the user that the event has been removed.
                Toast.makeText(context, "Event removed!", Toast.LENGTH_LONG).show();
                // Remove event from the DB.
                new DeleteEventTask(context, eventId).execute();
                // Reload calendar activity.
                context.startActivity(new Intent(context, CalendarActivity.class));
                break;
            case 503:
                Toast.makeText(context, "Google Maps not reachable!", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        calendarActivity.resumeNormalMode();
    }
}
