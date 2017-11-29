package it.polimi.travlendarplus.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String email;
    private String name;
    private String surname;
    private ArrayList<BreakEvent> breaks;
    private ArrayList<Event> events;
    private ArrayList<Ticket> heldTickets;
    private ArrayList<TypeOfEvent> preferences;
    private HashMap<String, Location> preferredLocations;

    public User(String email, String name, String surname, ArrayList<BreakEvent> breaks, ArrayList<Event> events, ArrayList<Ticket> heldTickets, ArrayList<TypeOfEvent> preferences, HashMap<String, Location> preferredLocations) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.breaks = breaks;
        this.events = events;
        this.heldTickets = heldTickets;
        this.preferences = preferences;
        this.preferredLocations = preferredLocations;
    }

    //constructor for user with empty ArrayLists
    public User(String email, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.breaks = new ArrayList<BreakEvent>();
        this.heldTickets = new ArrayList<Ticket>();
        this.preferences = new ArrayList<TypeOfEvent>();
        this.preferredLocations = new HashMap<String, Location>();
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

    public void setBreaks(ArrayList<BreakEvent> breaks) {
        this.breaks = breaks;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void setHeldTickets(ArrayList<Ticket> heldTickets) {
        this.heldTickets = heldTickets;
    }

    public void setPreferences(ArrayList<TypeOfEvent> preferences) {
        this.preferences = preferences;
    }

    public void setPreferredLocations(HashMap<String, Location> preferredLocations) {
        this.preferredLocations = preferredLocations;
    }
}
