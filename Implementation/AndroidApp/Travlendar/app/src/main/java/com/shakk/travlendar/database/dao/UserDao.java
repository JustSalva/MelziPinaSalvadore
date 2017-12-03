package com.shakk.travlendar.database.dao;

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

    @Insert
    void insertAll(User... users);

    @Update
    void updateUser(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM user WHERE email LIKE :email")
    User getUserByEmail(String email);

    @Query("SELECT name, surname FROM user WHERE email LIKE :email")
    FullName getUserFullNameByEmail(String email);

    @Query("SELECT * FROM user")
    List<User> getUsers();

    @Query("SELECT COUNT(*) FROM user")
    int countUsers();

    @Query("SELECT * FROM ticket " +
            "INNER JOIN user ON ticket.user_id = user.id " +
            "WHERE user.email LIKE :email")
    List<GenericTicketInfo> getUserTickets(String email);

    @Query("DELETE FROM user")
    void clearTable();
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