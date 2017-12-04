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
    void insertUser(User user);

    @Query("DELETE FROM user")
    void deleteUser();

    @Query("SELECT * FROM user WHERE email LIKE :email")
    LiveData<User> getUserByEmail(String email);

    @Query("SELECT name, surname FROM user WHERE email LIKE :email")
    LiveData<FullName> getUserFullNameByEmail(String email);

    @Query("SELECT * FROM user")
    LiveData<User> getUser();

    @Query("SELECT COUNT(*) FROM user")
    int countUsers();

    @Query("SELECT * FROM ticket " +
            "INNER JOIN user ON ticket.user_id = user.id " +
            "WHERE user.email LIKE :email")
    LiveData<List<GenericTicketInfo>> getUserTickets(String email);
}

class FullName {
    public String name;
    public String surname;
}

class GenericTicketInfo {
    public int id;
    public float cost;
    @ColumnInfo(name = "ticket_type")
    public String ticketType;
}