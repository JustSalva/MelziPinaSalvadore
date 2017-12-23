package it.polimi.travlendarplus.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import it.polimi.travlendarplus.database.entity.ticket.Ticket;

import java.util.List;

/**
 * Dao interface containing methods to access DB tickets values.
 */
@Dao
public interface TicketsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Ticket Ticket);

    @Update
    void update(Ticket ticket);

    @Delete
    void delete(Ticket ticket);

    @Query("DELETE FROM ticket")
    void deleteAll();

    @Query("SELECT * from ticket")
    LiveData<List<Ticket>> getTickets();

    @Query("SELECT * from ticket WHERE type LIKE 'Generic'")
    LiveData<List<Ticket>> getGenericTickets();

    @Query("SELECT * from ticket WHERE type LIKE 'Period'")
    LiveData<List<Ticket>> getPeriodTickets();

    @Query("SELECT * from ticket WHERE type LIKE 'Distance'")
    LiveData<List<Ticket>> getDistanceTickets();

    @Query("SELECT * from ticket WHERE type LIKE 'Path'")
    LiveData<List<Ticket>> getPathTickets();


}
