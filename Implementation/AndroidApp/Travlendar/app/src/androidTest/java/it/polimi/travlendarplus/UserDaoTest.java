package it.polimi.travlendarplus;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.dao.UserDao;
import it.polimi.travlendarplus.database.entity.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class UserDaoTest {

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
    public void addAndRemoveUser() throws Exception {
        User user = new User("10486221@polimi.it", "Alessandro", "Pina", "token");
        userDao.insert(user);
        assert userDao.countUsers() == 1;
        assert Objects.equals(userDao.getUser().getValue(), user);
        userDao.delete();
        assert userDao.countUsers() == 0;
    }

    @Test
    public void setTimestamp() throws Exception {
        User user = new User("10486221@polimi.it", "Alessandro", "Pina", "token");
        userDao.insert(user);
        long timestamp = 1000;
        userDao.setTimestamp(timestamp);
        assert Objects.equals(userDao.getUser().getValue().getTimestamp(), timestamp);
    }
}
