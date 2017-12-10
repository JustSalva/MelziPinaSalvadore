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

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room
                    .databaseBuilder(context, AppDatabase.class, "travlendarplus-database")
                    .build();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    public abstract UserDao userDao();
    public abstract CalendarDao calendarDao();
    public abstract TicketsDao ticketsDao();
}
