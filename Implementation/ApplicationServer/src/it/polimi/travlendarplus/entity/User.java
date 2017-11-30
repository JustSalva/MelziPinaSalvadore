package it.polimi.travlendarplus.entity;

import it.polimi.travlendarplus.entity.calendar.BreakEvent;
import it.polimi.travlendarplus.entity.calendar.Event;
import it.polimi.travlendarplus.entity.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entity.tickets.Ticket;

import java.util.*;

public class User {
    private String email;
    private String name;
    private String surname;
    private List<BreakEvent> breaks;
    private List<Event> events;
    private List<Ticket> heldTickets;
    private List<TypeOfEvent> preferences;
    private Map<String, Location> preferredLocations;

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
        this.events = new ArrayList<Event>();
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

    public List<BreakEvent> getBreaks() {
        return Collections.unmodifiableList(breaks);
    }

    public void setBreaks(List<BreakEvent> breaks) {
        this.breaks = breaks;
    }

    public void addBreak(BreakEvent event) {
        this.breaks.add(event);
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public List<Ticket> getHeldTickets() {
        return Collections.unmodifiableList(heldTickets);
    }

    public void setHeldTickets(List<Ticket> heldTickets) {
        this.heldTickets = heldTickets;
    }

    public void addTicket(Ticket ticket) {
        this.heldTickets.add(ticket);
    }

    public List<TypeOfEvent> getPreferences() {
        return Collections.unmodifiableList(preferences);
    }

    public void setPreferences(List<TypeOfEvent> preferences) {
        this.preferences = preferences;
    }

    public void addPreference(TypeOfEvent preference) {
        this.preferences.add(preference);
    }

    public Map<String, Location> getPreferredLocations() {
        return Collections.unmodifiableMap(preferredLocations);
    }

    public void setPreferredLocations(Map<String, Location> preferredLocations) {
        this.preferredLocations = preferredLocations;
    }

    public void addLocation(String name, Location location) {
        this.preferredLocations.put(name, location);
    }
}
