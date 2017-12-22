package it.polimi.travlendarplus.activity.handler.event;


import android.content.Context;
import android.content.Intent;
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

public class ScheduleEventHandler extends DefaultHandler {

    private CalendarActivity calendarActivity;

    public ScheduleEventHandler(Looper looper, Context context, CalendarActivity calendarActivity) {
        super(looper, context);
        this.context = context;
        this.calendarActivity = calendarActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                // Retrieve data from bundle.
                Toast.makeText(context, "Event scheduled successfully!", Toast.LENGTH_LONG).show();
                // Update DB with received events.
                String jsonEvents = msg.getData().getString("jsonEvents");
                List<EventResponse> events = new Gson()
                        .fromJson(jsonEvents, new TypeToken<List<EventResponse>>(){}.getType());
                new InsertEventsTask(context, events).execute();
                // Update DB with received break events.
                String jsonBreakEvents = msg.getData().getString("jsonBreakEvents");
                List<BreakEventResponse> breakEvents = new Gson()
                        .fromJson(jsonBreakEvents, new TypeToken<List<BreakEventResponse>>(){}.getType());
                new InsertBreakEventsTask(context, breakEvents).execute();
                break;
            default:
                break;
        }
        calendarActivity.resumeNormalMode();
    }
}