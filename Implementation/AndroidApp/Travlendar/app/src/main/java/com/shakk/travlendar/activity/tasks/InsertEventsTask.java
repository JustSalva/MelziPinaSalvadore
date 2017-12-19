package com.shakk.travlendar.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.event.Event;
import com.shakk.travlendar.database.entity.event.GenericEvent;
import com.shakk.travlendar.retrofit.response.EventResponse;

import java.util.List;

public class InsertEventsTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;
    private List<EventResponse> events;

    public InsertEventsTask(Context context, List<EventResponse> events) {
        this.database = AppDatabase.getInstance(context);
        this.events = events;
    }

    protected Void doInBackground(Void... voids) {
        for (EventResponse eventResponse : events) {
            GenericEvent genericEvent = new GenericEvent(
                    eventResponse.getId(),
                    eventResponse.getName(),
                    eventResponse.getStartingTime().getSeconds(),
                    eventResponse.getEndingTime().getSeconds(),
                    eventResponse.isScheduled()
            );
            Event event = new Event(
                    eventResponse.getDescription(),
                    eventResponse.getType().getId(),
                    eventResponse.getEventLocation().getAddress(),
                    eventResponse.isPrevLocChoice(),
                    eventResponse.isTravelAtLastChoice(),
                    eventResponse.getDeparture().getAddress()
            );
            genericEvent.setType(GenericEvent.EventType.EVENT);
            genericEvent.setEvent(event);
            database.calendarDao().insert(genericEvent);
        }
        database.userDao().setTimestamp(System.currentTimeMillis());
        return null;
    }
}
