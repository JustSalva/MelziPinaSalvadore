package com.shakk.travlendar.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.shakk.travlendar.database.entity.GenericEvent;
import com.shakk.travlendar.database.entity.Period;
import com.shakk.travlendar.database.entity.Ticket;
import com.shakk.travlendar.database.entity.Travel;
import com.shakk.travlendar.database.entity.TravelComponent;
import com.shakk.travlendar.database.entity.User;
import com.shakk.travlendar.database.dao.UserDao;

@Database(entities = {User.class, Ticket.class, GenericEvent.class, Period.class, Travel.class, TravelComponent.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract UserDao userDao();
}
