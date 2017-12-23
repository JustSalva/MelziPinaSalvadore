package it.polimi.travlendarplus.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.database.entity.TravelComponent;

import java.util.List;

/**
 * Dao interface containing methods to access DB calendar events and travel components.
 */
@Dao
public interface CalendarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GenericEvent genericEvent);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TravelComponent travelComponent);

    @Update
    void update(GenericEvent genericEvent);

    @Delete
    void delete(GenericEvent genericEvent);

    @Query("DELETE FROM generic_event WHERE id LIKE :id")
    void deleteEventFromId(int id);

    @Query("DELETE FROM generic_event")
    void deleteAll();

    @Query("SELECT * FROM generic_event " +
            "ORDER BY start_time")
    LiveData<List<GenericEvent>> getAllEvents();

    @Query("SELECT * FROM generic_event WHERE date LIKE :date " +
            "ORDER BY start_time")
    LiveData<List<GenericEvent>> getEvents(long date);

    @Query("SELECT * FROM generic_event WHERE type LIKE 'event' " +
            "AND date LIKE :date " +
            "AND scheduled LIKE 'true' " +
            "ORDER BY start_time")
    LiveData<List<GenericEvent>> getScheduledEvents(long date);

    @Query("SELECT * FROM generic_event WHERE type LIKE 'event' " +
            "AND date LIKE :date " +
            "AND scheduled LIKE 'false' " +
            "ORDER BY start_time")
    LiveData<List<GenericEvent>> getOverlappingEvents(long date);

    @Query("SELECT * FROM generic_event WHERE type LIKE 'break' AND date LIKE :date")
    LiveData<List<GenericEvent>> getBreakEvents(long date);

    @Query("SELECT * FROM travel_component WHERE event_id LIKE :eventId")
    LiveData<List<TravelComponent>> getTravelComponents(long eventId);

    @Query("SELECT * FROM travel_component")
    LiveData<List<TravelComponent>> getAllTravelComponents();

    @Query("DELETE FROM travel_component")
    void deleteTravelComponents();

    @Query("DELETE FROM travel_component WHERE event_id LIKE :eventId")
    void deleteEventTravelComponents(long eventId);
}
