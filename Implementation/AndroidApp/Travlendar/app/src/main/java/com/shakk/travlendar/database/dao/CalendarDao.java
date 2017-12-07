package com.shakk.travlendar.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.shakk.travlendar.database.entity.event.GenericEvent;
import com.shakk.travlendar.database.entity.TravelComponent;

import java.util.List;

@Dao
public interface CalendarDao {
    @Insert
    void insert(GenericEvent genericEvent);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<TravelComponent> travelComponents);

    @Update
    void update(GenericEvent genericEvent);

    @Delete
    void delete(GenericEvent genericEvent);

    @Query("SELECT * FROM generic_event WHERE type LIKE 'event' AND date LIKE :date")
    LiveData<List<GenericEvent>> getEvents(long date);

    @Query("SELECT * FROM generic_event WHERE type LIKE 'break' AND date LIKE :date")
    LiveData<List<GenericEvent>> getBreakEvents(long date);

    @Query("SELECT * FROM travel_component WHERE event_id LIKE :eventId")
    LiveData<List<TravelComponent>> getTravelComponents(int eventId);

    @Query("DELETE FROM travel_component")
    void deleteTravelComponents();
}
