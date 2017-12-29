package it.polimi.travlendarplus.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.TravelComponent;

import java.util.List;


/**
 * View model that allows access to travel components live data of the DB.
 */
public class EventViewModel extends AndroidViewModel {

    private AppDatabase database = AppDatabase
            .getInstance(getApplication()
                    .getApplicationContext());

    private LiveData<List<TravelComponent>> travelComponents;

    public EventViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * @param eventId id of the event.
     * @return All the travel components related to a event.
     */
    public LiveData<List<TravelComponent>> getTravelComponents(int eventId) {
        if (travelComponents == null) {
            travelComponents = database.calendarDao().getTravelComponentsByEventId(eventId);
        }
        return travelComponents;
    }
}
