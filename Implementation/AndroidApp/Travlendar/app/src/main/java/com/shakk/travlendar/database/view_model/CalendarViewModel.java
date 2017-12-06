package com.shakk.travlendar.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.GenericEvent;

import java.util.List;

public class CalendarViewModel extends AndroidViewModel {

    private AppDatabase database = AppDatabase
            .getAppDatabase(getApplication()
                    .getApplicationContext());

    private LiveData<List<GenericEvent>> events;
    private LiveData<List<GenericEvent>> breakEvents;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<GenericEvent>> getEvents(long date) {
        if (events == null) {
            events = new MutableLiveData<>();
            loadEvents(date);
        }
        return events;
    }

    public LiveData<List<GenericEvent>> getBreakEvents(long date) {
        if (breakEvents == null) {
            breakEvents = new MutableLiveData<>();
            loadBreakEvents(date);
        }
        return breakEvents;
    }

    private void loadEvents(long date) {
        events = database.genericEventDao().getEvents(date);
    }

    private void loadBreakEvents(long date) {
        events = database.genericEventDao().getBreakEvents(date);
    }
}
