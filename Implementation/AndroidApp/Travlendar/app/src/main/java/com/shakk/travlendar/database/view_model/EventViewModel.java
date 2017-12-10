package com.shakk.travlendar.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.TravelComponent;

import java.util.List;


public class EventViewModel extends AndroidViewModel {

    private AppDatabase database = AppDatabase
            .getInstance(getApplication()
                    .getApplicationContext());

    private LiveData<List<TravelComponent>> travelComponents;

    public EventViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<TravelComponent>> getTravelComponents(int eventId) {
        if (travelComponents == null) {
            travelComponents = new MutableLiveData<>();
            loadTravelComponents(eventId);
        }
        return travelComponents;
    }

    private void loadTravelComponents(int eventId) {
        travelComponents = database.calendarDao().getTravelComponents(eventId);
    }
}
