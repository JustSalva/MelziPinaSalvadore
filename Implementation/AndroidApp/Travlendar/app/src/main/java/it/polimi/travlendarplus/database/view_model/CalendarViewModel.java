package it.polimi.travlendarplus.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;

import java.util.List;

public class CalendarViewModel extends AndroidViewModel {

    private AppDatabase database;

    private LiveData<List<GenericEvent>> scheduledEvents;
    private LiveData<List<GenericEvent>> overlappingEvents;
    private LiveData<List<GenericEvent>> breakEvents;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
    }

    public LiveData<List<GenericEvent>> getScheduledEvents(long date) {
        if (scheduledEvents == null) {
            scheduledEvents = new MutableLiveData<>();
            scheduledEvents = database.calendarDao().getScheduledEvents(date);
        }
        return scheduledEvents;
    }

    public LiveData<List<GenericEvent>> getOverlappingEvents(long date) {
        if (overlappingEvents == null) {
            overlappingEvents = new MutableLiveData<>();
            overlappingEvents = database.calendarDao().getOverlappingEvents(date);
        }
        return overlappingEvents;
    }

    public LiveData<List<GenericEvent>> getBreakEvents(long date) {
        if (breakEvents == null) {
            breakEvents = new MutableLiveData<>();
            breakEvents = database.calendarDao().getBreakEvents(date);
        }
        return breakEvents;
    }
}
