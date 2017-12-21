package it.polimi.travlendarplus.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;

import java.util.List;


/**
 * View model that allows access to tickets live data of the DB.
 */
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

    /**
     * @return All the general tickets contained in the DB.
     */
    public LiveData<List<Ticket>> getGeneralTickets() {
        if (generalTickets == null) {
            generalTickets = database.ticketsDao().getGeneralTickets();
        }
        return generalTickets;
    }

    /**
     * @return All the period tickets contained in the DB.
     */
    public LiveData<List<Ticket>> getPeriodTicket() {
        if (periodTickets == null) {
            periodTickets = database.ticketsDao().getPeriodTickets();
        }
        return periodTickets;
    }

    /**
     * @return All the distance tickets contained in the DB.
     */
    public LiveData<List<Ticket>> getDistanceTicket() {
        if (distanceTickets == null) {
            distanceTickets = database.ticketsDao().getDistanceTickets();
        }
        return distanceTickets;
    }

    /**
     * @return All the path ticket contained in the DB.
     */
    public LiveData<List<Ticket>> getPathTicket() {
        if (pathTickets == null) {
            pathTickets = database.ticketsDao().getPathTickets();
        }
        return pathTickets;
    }
}
