package it.polimi.travlendarplus.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import it.polimi.travlendarplus.database.entity.User;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("DELETE FROM user")
    void delete();

    @Query("SELECT * FROM user")
    LiveData<User> getUser();

    @Query("UPDATE user SET timestamp = :timestamp")
    void setTimestamp(long timestamp);

    @Query("SELECT COUNT(*) FROM user")
    int countUsers();
}