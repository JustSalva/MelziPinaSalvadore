package com.shakk.travlendar;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.dao.UserDao;
import com.shakk.travlendar.database.entity.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private UserDao userDao;
    private AppDatabase database;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        userDao = database.userDao();
    }

    @After
    public void closeDb() throws IOException {
        database.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        User user = new User("10486221@polimi.it", "Alessandro", "Pina");
        userDao.insert(user);
        assert userDao.countUsers() == 1;
        assert userDao.getUser().getValue().getName().equals("Alessandro");
    }
}
