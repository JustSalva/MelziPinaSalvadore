package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.tasks.DeleteEventTask;

/**
 * Handler that handles the server response to the event deletion.
 * It is used by the CalendarActivity.
 */
public class DeleteEventHandler extends Handler {

    private Context context;
    private CalendarActivity calendarActivity;

    public DeleteEventHandler(Looper looper, Context context, CalendarActivity calendarActivity) {
        super(looper);
        this.context = context;
        this.calendarActivity = calendarActivity;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 0:
                Toast.makeText(context, "No internet connection available!", Toast.LENGTH_LONG).show();
                break;
            case 200:
                int eventId = msg.getData().getInt("Id");
                // Notify the user that the event has been removed.
                Toast.makeText(context, "Event removed!", Toast.LENGTH_LONG).show();
                // Remove event from the DB.
                new DeleteEventTask(context, eventId).execute();
                // Reload calendar activity.
                context.startActivity(new Intent(context, CalendarActivity.class));
                break;
            case 400:
                Toast.makeText(context, "The event specified does not exist!", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                break;
        }
        calendarActivity.resumeNormalMode();
    }
}
