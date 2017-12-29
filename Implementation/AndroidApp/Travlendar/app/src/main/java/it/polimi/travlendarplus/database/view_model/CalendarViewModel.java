package it.polimi.travlendarplus.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.TravelComponent;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;

import java.util.List;

/**
 * View model that allows access to events live data of the DB.
 */
public class CalendarViewModel extends AndroidViewModel {

    private AppDatabase database;

    private LiveData<List<GenericEvent>> events;
    private LiveData<List<GenericEvent>> scheduledEvents;
    private LiveData<List<GenericEvent>> overlappingEvents;
    private LiveData<List<GenericEvent>> breakEvents;
    private LiveData<List<TravelComponent>> travelComponents;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
    }

    /**
     * @return All the events contained in the DB.
     */
    public LiveData<List<GenericEvent>> getAllEvents() {
        if (events == null) {
            events = new MutableLiveData<>();
            events = database.calendarDao().getAllEvents();
        }
        return events;
    }

    /**
     * @param date long representing a day (unix time).
     * @return All the events happening in a given day.
     */
    public LiveData<List<GenericEvent>> getEvents(long date) {
        if (events == null) {
            events = new MutableLiveData<>();
            events = database.calendarDao().getEvents(date);
        }
        return events;
    }

    /**
     * @param date long representing a day (unix time).
     * @return All the scheduled events happening in a given day.
     */
    public LiveData<List<GenericEvent>> getScheduledEvents(long date) {
        if (scheduledEvents == null) {
            scheduledEvents = new MutableLiveData<>();
            scheduledEvents = database.calendarDao().getScheduledEvents(date);
        }
        return scheduledEvents;
    }

    /**
     * @param date long representing a day (unix time).
     * @return All the overlapping events happening in a given day.
     */
    public LiveData<List<GenericEvent>> getOverlappingEvents(long date) {
        if (overlappingEvents == null) {
            overlappingEvents = new MutableLiveData<>();
            overlappingEvents = database.calendarDao().getOverlappingEvents(date);
        }
        return overlappingEvents;
    }

    /**
     * @param date long representing a day (unix time).
     * @return All the break events happening in a given day.
     */
    public LiveData<List<GenericEvent>> getBreakEvents(long date) {
        if (breakEvents == null) {
            breakEvents = new MutableLiveData<>();
            breakEvents = database.calendarDao().getBreakEvents(date);
        }
        return breakEvents;
    }

    /**
     * @return All the travel components present in the DB.
     */
    public LiveData<List<TravelComponent>> getAllTravelComponents() {
        return database.calendarDao().getAllTravelComponents();
    }
}
