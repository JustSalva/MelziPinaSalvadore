package it.polimi.travlendarplus.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import it.polimi.travlendarplus.database.entity.ticket.Ticket;

/**
 * Dao interface containing methods to access DB tickets values.
 */
@Dao
public interface TicketsDao {
    /**
     * Insert a list of tickets in the DB.
     *
     * @param tickets A list of tickets to be inserted.
     */
    @Insert( onConflict = OnConflictStrategy.REPLACE )
    void insert ( List < Ticket > tickets );

    /**
     * Update a ticket already present in the DB.
     *
     * @param ticket Ticket to be updated.
     */
    @Update
    void update ( Ticket ticket );

    /**
     * Delete a ticket from the DB.
     *
     * @param ticket Ticket to be deleted.
     */
    @Delete
    void delete ( Ticket ticket );

    /**
     * Delete a ticket from the DB.
     *
     * @param eventId Id of the ticket to be deleted.
     */
    @Query( "DELETE FROM ticket WHERE id LIKE :eventId" )
    void deleteFromId ( int eventId );

    /**
     * Deletes all the tickets present from the DB.
     */
    @Query( "DELETE FROM ticket" )
    void deleteAll ();

    /**
     * Returns all the tickets present in the DB.
     *
     * @return A list of ticket.
     */
    @Query( "SELECT * from ticket" )
    LiveData < List < Ticket > > getTickets ();

    /**
     * Removes a ticket from all the travel components.
     *
     * @param ticketId Id of the ticket to be removed.
     */
    @Query( "UPDATE travel_component SET ticket_id = 0 WHERE ticket_id LIKE :ticketId" )
    void removeTicketFromTravelComponent ( int ticketId );
}
