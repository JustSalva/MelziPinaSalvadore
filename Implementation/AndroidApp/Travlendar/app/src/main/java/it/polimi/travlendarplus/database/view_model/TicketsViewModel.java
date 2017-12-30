package it.polimi.travlendarplus.database.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;


/**
 * View model that allows access to tickets live data of the DB.
 */
public class TicketsViewModel extends AndroidViewModel {

    private AppDatabase database = AppDatabase
            .getInstance( getApplication()
                    .getApplicationContext() );

    private LiveData < List < Ticket > > tickets;

    public TicketsViewModel ( @NonNull Application application ) {
        super( application );
    }

    /**
     * @return All the tickets contained in the DB.
     */
    public LiveData < List < Ticket > > getTickets () {
        if ( tickets == null ) {
            tickets = database.ticketsDao().getTickets();
        }
        return tickets;
    }
}
