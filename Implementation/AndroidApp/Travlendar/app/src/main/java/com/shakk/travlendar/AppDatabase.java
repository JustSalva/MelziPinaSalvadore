package com.shakk.travlendar;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.shakk.travlendar.database.entities.GenericEvent;
import com.shakk.travlendar.database.entities.Period;
import com.shakk.travlendar.database.entities.Ticket;
import com.shakk.travlendar.database.entities.Travel;
import com.shakk.travlendar.database.entities.TravelComponent;
import com.shakk.travlendar.database.entities.User;
import com.shakk.travlendar.database.daos.UserDao;

@Database(entities = {User.class, Ticket.class, GenericEvent.class, Period.class, Travel.class, TravelComponent.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract UserDao userDao();
}
