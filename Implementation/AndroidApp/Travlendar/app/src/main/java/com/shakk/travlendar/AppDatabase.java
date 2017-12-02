package com.shakk.travlendar;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.shakk.travlendar.database.GenericEvent;
import com.shakk.travlendar.database.Period;
import com.shakk.travlendar.database.Ticket;
import com.shakk.travlendar.database.Travel;
import com.shakk.travlendar.database.TravelComponent;
import com.shakk.travlendar.database.User;
import com.shakk.travlendar.database.UserDao;

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
