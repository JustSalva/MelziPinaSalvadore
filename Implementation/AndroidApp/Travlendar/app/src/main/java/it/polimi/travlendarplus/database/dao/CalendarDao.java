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

    // EVENTS.

    /**
     * Inserts a generic event.
     * @param genericEvent Generic event to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGenericEvent(GenericEvent genericEvent);

    /**
     * @return Number of generic events present in the DB.
     */
    @Query("SELECT COUNT(*) FROM generic_event")
    int countGenericEvents();

    /**
     * Updates a generic event.
     * @param genericEvent Generic event to be updated.
     */
    @Update
    void updateGenericEvent(GenericEvent genericEvent);

    /**
     * Deletes a generic event.
     * @param genericEvent Generic event to be deleted.
     */
    @Delete
    void deleteGenericEvent(GenericEvent genericEvent);

    /**
     * Deletes a generic event with a certain id.
     * @param id Id of the generic event to be deleted.
     */
    @Query("DELETE FROM generic_event WHERE id LIKE :id")
    void deleteGenericEventFromId(int id);

    /**
     * Deletes all the generic events present in the DB.
     */
    @Query("DELETE FROM generic_event")
    void deleteAllGenericEvents();

    /**
     * Gets all the generic events present in the DB.
     * @return A list of all the generic events.
     */
    @Query("SELECT * FROM generic_event " +
            "ORDER BY start_time")
    LiveData<List<GenericEvent>> getAllGenericEvents();

    /**
     * Gets all the generic events happening on a specific date.
     * @param date UTC time of the date.
     * @return A list of generic events happening on a date.
     */
    @Query("SELECT * FROM generic_event WHERE date LIKE :date " +
            "ORDER BY start_time")
    LiveData<List<GenericEvent>> getGenericEventsByDate(long date);

    /**
     * Gets all the scheduled events happening on a specific date.
     * @param date UTC time of the date.
     * @return A list of all the scheduled events happening on a date.
     */
    @Query("SELECT * FROM generic_event WHERE type LIKE 'event' " +
            "AND date LIKE :date " +
            "AND scheduled LIKE 'true' " +
            "ORDER BY start_time")
    LiveData<List<GenericEvent>> getScheduledEventsByDate(long date);

    /**
     * Gets all the overlapping events happening on a specific date.
     * @param date UTC time of the date.
     * @return A list of all the overlapping events happening on a date.
     */
    @Query("SELECT * FROM generic_event WHERE type LIKE 'event' " +
            "AND date LIKE :date " +
            "AND scheduled LIKE 'false' " +
            "ORDER BY start_time")
    LiveData<List<GenericEvent>> getOverlappingEventsByDate(long date);

    /**
     * Gets all the break events happening on a specific date.
     * @param date UTC time of the date.
     * @return A list of all the break events happening on a date.
     */
    @Query("SELECT * FROM generic_event WHERE type LIKE 'break' AND date LIKE :date")
    LiveData<List<GenericEvent>> getBreakEventsByDate(long date);

    // TRAVEL COMPONENTS.

    /**
     * Inserts a travel component in the DB.
     * @param travelComponent Travel component to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTravelComponent(TravelComponent travelComponent);

    /**
     * Gets all the travel components related to an event.
     * @param eventId Id of the event related.
     * @return List of the travel components related to an event.
     */
    @Query("SELECT * FROM travel_component WHERE event_id LIKE :eventId")
    LiveData<List<TravelComponent>> getTravelComponentsByEventId(long eventId);

    /**
     * Gets all the travel components present in the DB.
     * @return A list of all the travel component.
     */
    @Query("SELECT * FROM travel_component")
    LiveData<List<TravelComponent>> getAllTravelComponents();

    /**
     * Deletes all the travel components present in the DB.
     */
    @Query("DELETE FROM travel_component")
    void deleteAllTravelComponents();

    /**
     * Deletes all the travel components related to an event.
     * @param eventId Id of the event related.
     */
    @Query("DELETE FROM travel_component WHERE event_id LIKE :eventId")
    void deleteEventTravelComponents(long eventId);

    /**
     * Add a ticket to a travel component.
     * @param ticketId Id of the ticket.
     * @param travelComponentId Id of the travel component.
     */
    @Query("UPDATE travel_component SET ticket_id = :ticketId WHERE id LIKE :travelComponentId")
    void selectTicket(int ticketId, int travelComponentId);

    /**
     * Remove a ticket from a travel component.
     * @param travelComponentId Id of the travel component.
     */
    @Query("UPDATE travel_component SET ticket_id = 0 WHERE id LIKE :travelComponentId")
    void deselectTicket(int travelComponentId);
}
