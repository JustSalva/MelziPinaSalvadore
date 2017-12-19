package com.shakk.travlendar.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.event.BreakEvent;
import com.shakk.travlendar.database.entity.event.GenericEvent;
import com.shakk.travlendar.retrofit.response.BreakEventResponse;

import java.util.List;

public class InsertBreakEventsTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;
    private List<BreakEventResponse> breakEvents;

    public InsertBreakEventsTask(Context context, List<BreakEventResponse> breakEvents) {
        this.database = AppDatabase.getInstance(context);
        this.breakEvents = breakEvents;
    }

    protected Void doInBackground(Void... voids) {
        for (BreakEventResponse breakEventResponse : breakEvents) {
            GenericEvent genericEvent = new GenericEvent(
                    breakEventResponse.getId(),
                    breakEventResponse.getName(),
                    breakEventResponse.getStartingTime().getSeconds(),
                    breakEventResponse.getEndingTime().getSeconds(),
                    breakEventResponse.isScheduled()
            );
            BreakEvent breakEvent = new BreakEvent(
                    breakEventResponse.getMinimumTime()
            );
            genericEvent.setType(GenericEvent.EventType.BREAK);
            genericEvent.setBreakEvent(breakEvent);
            database.calendarDao().insert(genericEvent);
        }
        database.userDao().setTimestamp(System.currentTimeMillis());
        return null;
    }
}
