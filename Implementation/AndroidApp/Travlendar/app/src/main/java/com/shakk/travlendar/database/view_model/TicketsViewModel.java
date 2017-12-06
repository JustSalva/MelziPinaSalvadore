package com.shakk.travlendar.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.Ticket;

import java.util.List;


public class TicketsViewModel extends AndroidViewModel {

    private AppDatabase database = AppDatabase
            .getAppDatabase(getApplication()
                    .getApplicationContext());

    private LiveData<List<Ticket>> generalTickets;
    private LiveData<List<Ticket>> periodTickets;
    private LiveData<List<Ticket>> distanceTickets;

    public TicketsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Ticket>> getGeneralTickets() {
        if (generalTickets == null) {
            generalTickets = new MutableLiveData<>();
            loadGeneralTickets();
        }
        return generalTickets;
    }

    public LiveData<List<Ticket>> getPeriodTicket() {
        if (periodTickets == null) {
            periodTickets = new MutableLiveData<>();
            loadPeriodTicket();
        }
        return periodTickets;
    }

    public LiveData<List<Ticket>> getDistanceTicket() {
        if (distanceTickets == null) {
            distanceTickets = new MutableLiveData<>();
            loadDistanceTicket();
        }
        return distanceTickets;
    }

    private void loadGeneralTickets() {
        generalTickets = database.ticketsDao().getGeneralTickets();
    }

    private void loadPeriodTicket() {
        periodTickets = database.ticketsDao().getPeriodTickets();
    }

    private void loadDistanceTicket() {
        distanceTickets = database.ticketsDao().getDistanceTickets();
    }
}
