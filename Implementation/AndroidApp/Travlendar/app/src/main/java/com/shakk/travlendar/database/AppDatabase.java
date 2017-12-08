package com.shakk.travlendar.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.shakk.travlendar.database.dao.CalendarDao;
import com.shakk.travlendar.database.dao.TicketsDao;
import com.shakk.travlendar.database.entity.event.GenericEvent;
import com.shakk.travlendar.database.entity.ticket.Ticket;
import com.shakk.travlendar.database.entity.TravelComponent;
import com.shakk.travlendar.database.entity.User;
import com.shakk.travlendar.database.dao.UserDao;

@Database(entities = {User.class, Ticket.class, GenericEvent.class, TravelComponent.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "travlendar-database")
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract UserDao userDao();
    public abstract CalendarDao calendarDao();
    public abstract TicketsDao ticketsDao();
}
