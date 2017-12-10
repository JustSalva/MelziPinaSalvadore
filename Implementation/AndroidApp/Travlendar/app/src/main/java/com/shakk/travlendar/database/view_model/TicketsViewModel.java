package com.shakk.travlendar.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.ticket.Ticket;

import java.util.List;


public class TicketsViewModel extends AndroidViewModel {

    private AppDatabase database = AppDatabase
            .getInstance(getApplication()
                    .getApplicationContext());

    private LiveData<List<Ticket>> generalTickets;
    private LiveData<List<Ticket>> periodTickets;
    private LiveData<List<Ticket>> distanceTickets;
    private LiveData<List<Ticket>> pathTickets;

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
            loadPeriodTickets();
        }
        return periodTickets;
    }

    public LiveData<List<Ticket>> getDistanceTicket() {
        if (distanceTickets == null) {
            distanceTickets = new MutableLiveData<>();
            loadDistanceTickets();
        }
        return distanceTickets;
    }

    public LiveData<List<Ticket>> getPathTicket() {
        if (pathTickets == null) {
            pathTickets = new MutableLiveData<>();
            loadPathTickets();
        }
        return pathTickets;
    }

    private void loadGeneralTickets() {
        generalTickets = database.ticketsDao().getGeneralTickets();
    }

    private void loadPeriodTickets() {
        periodTickets = database.ticketsDao().getPeriodTickets();
    }

    private void loadDistanceTickets() {
        distanceTickets = database.ticketsDao().getDistanceTickets();
    }

    private void loadPathTickets() {
        pathTickets = database.ticketsDao().getPathTickets();
    }
}
