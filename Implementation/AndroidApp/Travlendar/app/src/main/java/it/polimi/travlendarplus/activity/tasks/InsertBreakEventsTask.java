package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.event.BreakEvent;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.retrofit.response.BreakEventResponse;

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
        database.userDao().setTimestamp(System.currentTimeMillis()/1000L);
        return null;
    }
}
