package com.shakk.travlendar.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.shakk.travlendar.database.entity.User;

import java.util.List;

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