package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.tickets.Ticket;

import javax.persistence.*;
import java.util.*;

@Entity(name = "USER")
public class User extends GenericEntity {

    private static final long serialVersionUID = 771264263714054170L;

    @Id
    private String email;

    @Column( name = "NAME" )
    private String name;

    @Column( name = "SURNAME" )
    private String surname;

    @Column( name = "PASSWORD" )
    private String password;

    @JoinTable( name = "USER_DEVICES" )
    @OneToMany( mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List<UserDevice> userDevices;

    @JoinTable( name = "USER_BREAK_EVENTS" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List<BreakEvent> breaks;

    @JoinTable( name = "USER_EVENTS" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List<Event> events;

    @JoinTable( name = "USER_TICKETS" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List<Ticket> heldTickets;

    @JoinTable( name = "USER_PREFERENCES" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List<TypeOfEvent> preferences;

    @ElementCollection( fetch = FetchType.LAZY )
    @MapKeyColumn( name="PREFERRED_LOCATIONS" )
    @Column( name="NAME" )
    @CollectionTable( name = "USER_PREFERRED_LOCATIONS" )
    @JoinColumns({
            @JoinColumn( name="EVENT_LATITUDE", referencedColumnName="latitude" ),
            @JoinColumn( name="EVENT_LONGITUDE", referencedColumnName="longitude" )
    })
    private Map<Location, String> preferredLocations;

    @Embedded
    private Timestamp lastUpdate;

    public User() {
        this.lastUpdate = new Timestamp();
    }

    public User(String email, String name, String surname, String password,
                List<UserDevice> userDevices, List<BreakEvent> breaks, List<Event> events,
                List<Ticket> heldTickets, List<TypeOfEvent> preferences,
                Map<Location, String> preferredLocations) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.userDevices = userDevices;
        this.breaks = breaks;
        this.events = events;
        this.heldTickets = heldTickets;
        this.preferences = preferences;
        this.preferredLocations = preferredLocations;
        this.lastUpdate = new Timestamp();
    }

    //constructor for user with empty ArrayLists
    public User(String email, String name, String surname, String password) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.userDevices = new ArrayList<UserDevice>();
        this.breaks = new ArrayList<BreakEvent>();
        this.events = new ArrayList<Event>();
        this.heldTickets = new ArrayList<Ticket>();
        this.preferences = new ArrayList<TypeOfEvent>();
        this.preferredLocations = new HashMap<Location, String>();
        this.lastUpdate = new Timestamp();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserDevice> getUserDevices() {
        return userDevices;
    }

    public void setUserDevices(List<UserDevice> userDevices) {
        this.userDevices = userDevices;
    }

    public void addUserDevice( String idDevice ) {
        this.userDevices.add( new UserDevice( idDevice, this ) );
    }

    public void removeUserDevice( String idDevice ) {
        userDevices.removeIf(userDevice -> userDevice.getIdDevice().equals( idDevice ));
    }

    /**
     * Retrieve a UserDevice of the user
     * @param idDevice id of the requested UserDevice
     * @return the requested UserDevice class instance or null if it doesn't exist
     */
    public UserDevice getUserDevice( String idDevice ) {
        for( UserDevice userDevice:userDevices ){
            if( userDevice.getIdDevice().equals( idDevice ) ){
                return userDevice;
            }
        }
        return null;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
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

    public Map<Location, String> getPreferredLocations() {
        return Collections.unmodifiableMap(preferredLocations);
    }

    public void setPreferredLocations(Map<Location, String> preferredLocations) {
        this.preferredLocations = preferredLocations;
    }

    public void addLocation(String name, Location location) {
        this.preferredLocations.put(location, name);
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public static User load(String key){
        return GenericEntity.load( User.class, key );
    }

    @Override
    public boolean isAlreadyInDb() {
        return load(email) != null;
    }
}