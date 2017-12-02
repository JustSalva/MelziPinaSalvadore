package com.shakk.travlendar.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity
public class User {
    @PrimaryKey
    private String email;
    private String name;
    private String surname;
    @ColumnInfo(name = "univocal_code")
    private String univocalCode;
    private String timestamp;
    private List<GenericEvent> events;
    @ColumnInfo(name = "break_events")
    private List<GenericEvent> breakEvents;
    private List<Ticket> tickets;

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

    public String getUnivocalCode() {
        return univocalCode;
    }

    public void setUnivocalCode(String univocalCode) {
        this.univocalCode = univocalCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<GenericEvent> getEvents() {
        return events;
    }

    public void setEvents(List<GenericEvent> events) {
        this.events = events;
    }

    public List<GenericEvent> getBreakEvents() {
        return breakEvents;
    }

    public void setBreakEvents(List<GenericEvent> breakEvents) {
        this.breakEvents = breakEvents;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
