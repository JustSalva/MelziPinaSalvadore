package it.polimi.travlendarplus.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import it.polimi.travlendarplus.database.dao.CalendarDao;
import it.polimi.travlendarplus.database.dao.TicketsDao;
import it.polimi.travlendarplus.database.dao.UserDao;
import it.polimi.travlendarplus.database.entity.TravelComponent;
import it.polimi.travlendarplus.database.entity.User;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;

/**
 * Database class defining all the different entities contained in it.
 */
@Database( entities = { User.class, Ticket.class, GenericEvent.class, TravelComponent.class }, version = 1 )
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public static AppDatabase getInstance ( Context context ) {
        if ( instance == null ) {
            instance = Room
                    .databaseBuilder( context, AppDatabase.class, "travlendarplus-database" )
                    .build();
        }
        return instance;
    }

    public static void destroyInstance () {
        instance = null;
    }

    public abstract UserDao userDao ();

    public abstract CalendarDao calendarDao ();

    public abstract TicketsDao ticketsDao ();
}
