package it.polimi.travlendarplus.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * DB user entity.
 */
@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String email;
    private String name;
    private String surname;
    private String token;
    private long timestamp;

    public User(String email, String name, String surname, String token) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.token = token;
        this.timestamp = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}