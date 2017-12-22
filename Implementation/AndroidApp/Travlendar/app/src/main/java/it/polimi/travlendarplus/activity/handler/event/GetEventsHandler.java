package it.polimi.travlendarplus.activity.handler.event;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.activity.tasks.InsertBreakEventsTask;
import it.polimi.travlendarplus.activity.tasks.InsertEventsTask;
import it.polimi.travlendarplus.retrofit.response.BreakEventResponse;
import it.polimi.travlendarplus.retrofit.response.EventResponse;

/**
 * Handler that handles the server response to the events request.
 * It is used by the CalendarActivity.
 */
public class GetEventsHandler extends DefaultHandler {

    private CalendarActivity calendarActivity;

    public GetEventsHandler(Looper looper, Context context, CalendarActivity calendarActivity) {
        super(looper, context);
        this.calendarActivity = calendarActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                Toast.makeText(context, "Events updated!", Toast.LENGTH_LONG).show();
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                Log.d("EVENTS_JSON", bundle.getString("jsonEvents"));
                // Save events from JSON.
                String jsonEvents = bundle.getString("jsonEvents");
                List<EventResponse> events = new Gson().fromJson(
                        jsonEvents,
                        new TypeToken<List<EventResponse>>(){}.getType()
                );
                Log.d("BREAK_EVENTS_JSON", bundle.getString("jsonBreakEvents"));
                // Save break events from JSON.
                String jsonBreakEvents = bundle.getString("jsonBreakEvents");
                List<BreakEventResponse> breakEvents = new Gson().fromJson(
                        jsonBreakEvents,
                        new TypeToken<List<BreakEventResponse>>(){}.getType()
                );
                // Write new events into DB.
                new InsertEventsTask(context, events).execute();
                // Write new break events into DB.
                new InsertBreakEventsTask(context, breakEvents).execute();
                break;
            default:
                break;
        }
        calendarActivity.resumeNormalMode();
    }
}
