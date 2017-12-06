package com.shakk.travlendar.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.shakk.travlendar.database.entity.Ticket;

import java.util.List;

@Dao
public interface TicketsDao {
    @Insert
    void insert(Ticket Ticket);

    @Update
    void update(Ticket ticket);

    @Delete
    void delete(Ticket ticket);

    @Query("SELECT * from ticket WHERE ticket_type LIKE general_ticket")
    LiveData<List<Ticket>> getGeneralTickets();

    @Query("SELECT * from ticket WHERE ticket_type LIKE period_ticket")
    LiveData<List<Ticket>> getPeriodTickets();

    @Query("SELECT * from ticket WHERE ticket_type LIKE distance_ticket")
    LiveData<List<Ticket>> getDistanceTickets();
}
