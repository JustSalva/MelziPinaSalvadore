package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.CalendarActivity;

public class ScheduleEventHandler extends Handler {

    private Context context;
    private CalendarActivity calendarActivity;

    public ScheduleEventHandler(Looper looper, Context context, CalendarActivity calendarActivity) {
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
                // Retrieve data from bundle.
                Toast.makeText(context, "Event scheduled successfully!", Toast.LENGTH_LONG).show();
                break;
            case 400:
                Toast.makeText(context, "The id of the event to be scheduled does not exist!", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, CalendarActivity.class));
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                Log.d("ERROR_RESPONSE", msg.toString());
                break;
        }
        calendarActivity.resumeNormalMode();
    }
}
