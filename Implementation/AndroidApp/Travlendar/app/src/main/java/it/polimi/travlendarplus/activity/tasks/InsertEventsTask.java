package it.polimi.travlendarplus.activity.tasks;


import android.content.Context;
import android.os.AsyncTask;

import it.polimi.travlendarplus.MiniTravel;
import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.TravelComponent;
import it.polimi.travlendarplus.database.entity.event.Event;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.retrofit.response.event.EventResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Task that inserts a list of events in the DB.
 */
public class InsertEventsTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;
    private List<EventResponse> events;

    public InsertEventsTask(Context context, List<EventResponse> events) {
        this.database = AppDatabase.getInstance(context);
        this.events = events;
    }

    protected Void doInBackground(Void... voids) {
        for (EventResponse eventResponse : events) {
            // If the event is already present, delete it.
            database.calendarDao().deleteEventFromId((int) eventResponse.getId());
            // Create generic event.
            GenericEvent genericEvent = new GenericEvent(
                    eventResponse.getId(),
                    eventResponse.getName(),
                    eventResponse.getStartingTime().getSeconds(),
                    eventResponse.getEndingTime().getSeconds(),
                    eventResponse.isScheduled()
            );
            // Create event.
            Event event = new Event(
                    eventResponse.getDescription(),
                    eventResponse.getType().getId(),
                    eventResponse.getEventLocation().getAddress(),
                    eventResponse.isPrevLocChoice(),
                    eventResponse.isTravelAtLastChoice(),
                    eventResponse.getDeparture().getAddress()
            );
            // Set the event in the generic event.
            genericEvent.setType(GenericEvent.EventType.EVENT);
            genericEvent.setEvent(event);
            // Insert the generic event in the DB.
            database.calendarDao().insert(genericEvent);

            // Delete all the old travel components for the event.
            database.calendarDao().deleteEventTravelComponents(eventResponse.getId());
            // If scheduled, get the travel components for the event.
            if (eventResponse.isScheduled()) {
                List<MiniTravel> miniTravels = eventResponse.getFeasiblePath().getMiniTravels();
                for (MiniTravel miniTravel : miniTravels) {
                    TravelComponent travelComponent = new TravelComponent(
                            miniTravel.getId(),
                            miniTravel.getLength(),
                            eventResponse.getId(),
                            miniTravel.getMeanUsed().getType(),
                            miniTravel.getDeparture().getAddress(),
                            miniTravel.getArrival().getAddress(),
                            miniTravel.getStartingTime().getSeconds(),
                            miniTravel.getEndingTime().getSeconds()
                    );
                    // Insert travel component in the DB.
                    database.calendarDao().insert(travelComponent);
                }
            }
        }
        // Update timestamp in the user table of the DB.
        database.userDao().setTimestamp(System.currentTimeMillis()/1000L);
        return null;
    }
}
