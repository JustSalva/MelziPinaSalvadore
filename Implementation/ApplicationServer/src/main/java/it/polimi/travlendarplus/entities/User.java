package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.tickets.Ticket;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This JPA class represent a Travlendar+ registered user
 */
@Entity( name = "TRAVLENDAR_USER" )
public class User extends GenericEntity {

    private static final long serialVersionUID = 771264263714054170L;

    /**
     * Primary key - user's email
     */
    @Id
    private String email;

    /**
     * User's name
     */
    @Column( name = "NAME" )
    private String name;

    /**
     * User's surname
     */
    @Column( name = "SURNAME" )
    private String surname;

    /**
     * User's password
     */
    @Column( name = "PASSWORD" )
    private String password;

    /**
     * List the user's devices
     */
    @JoinTable( name = "USER_DEVICES" )
    @OneToMany( mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List < UserDevice > userDevices;

    /**
     * List of either scheduled or not scheduled break events of the user
     */
    @JoinTable( name = "USER_BREAK_EVENTS" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List < BreakEvent > breaks;

    /**
     * List of either scheduled or not scheduled events of the user
     */
    @JoinTable( name = "USER_EVENTS" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List < Event > events;

    /**
     * List of tickets saved into the user's profile
     */
    @JoinTable( name = "USER_TICKETS" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List < Ticket > heldTickets;

    /**
     * List of user's preference profiles
     */
    @JoinTable( name = "USER_PREFERENCES" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List < TypeOfEvent > preferences;

    /**
     * list of user's saved locations
     */
    @JoinTable( name = "USER_PREFERRED_LOCATIONS" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List < UserLocation > preferredLocations;

    /**
     * Timestamp in Unix time that memorize the last update of an RSA key pair
     */
    @Embedded
    private Timestamp lastUpdate;

    public User () {
        this.lastUpdate = new Timestamp();
        this.userDevices = new ArrayList < UserDevice >();
        this.breaks = new ArrayList < BreakEvent >();
        this.events = new ArrayList < Event >();
        this.heldTickets = new ArrayList < Ticket >();
        this.preferences = new ArrayList < TypeOfEvent >();
        this.preferredLocations = new ArrayList < UserLocation >();
    }

    public User ( String email, String name, String surname, String password,
                  List < UserDevice > userDevices, List < BreakEvent > breaks, List < Event > events,
                  List < Ticket > heldTickets, List < TypeOfEvent > preferences,
                  List < UserLocation > preferredLocations ) {
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
    public User ( String email, String name, String surname, String password ) {
        this();
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;
    }

    /**
     * Allows to load a User class from the database
     *
     * @param key primary key of the user tuple
     * @return the requested tuple as a User class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static User load ( String key ) throws EntityNotFoundException {
        return GenericEntity.load( User.class, key );
    }

    public String getEmail () {
        return email;
    }

    public void setEmail ( String email ) {
        this.email = email;
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public String getSurname () {
        return surname;
    }

    public void setSurname ( String surname ) {
        this.surname = surname;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword ( String password ) {
        this.password = password;
    }

    public List < UserDevice > getUserDevices () {
        return userDevices;
    }

    public void setUserDevices ( List < UserDevice > userDevices ) {
        this.userDevices = userDevices;
    }

    /**
     * Adds in the user's devices list a new device
     *
     * @param idDevice identifier of the new device
     */
    public void addUserDevice ( String idDevice ) {
        this.userDevices.add( new UserDevice( idDevice, this ) );
    }

    /**
     * Removes an user's device from the devices list, if present
     *
     * @param idDevice identifier of the device to be removed
     */
    public void removeUserDevice ( String idDevice ) {
        userDevices.removeIf( userDevice -> userDevice.getIdDevice().equals( idDevice ) );
    }

    /**
     * Retrieve a UserDevice of the user
     *
     * @param idDevice id of the requested UserDevice
     * @return the requested UserDevice class instance or null if it doesn't exist
     */
    public UserDevice getUserDevice ( String idDevice ) {
        for ( UserDevice userDevice : userDevices ) {
            if ( userDevice.getIdDevice().equals( idDevice ) ) {
                return userDevice;
            }
        }
        return null;
    }

    public Timestamp getLastUpdate () {
        return lastUpdate;
    }

    public void setLastUpdate ( Timestamp lastUpdate ) {
        this.lastUpdate = lastUpdate;
    }

    public List < BreakEvent > getBreaks () {
        return Collections.unmodifiableList( breaks );
    }

    public void setBreaks ( List < BreakEvent > breaks ) {
        this.breaks = breaks;
    }

    /**
     * Adds in the user's breakEvents list a new break event
     *
     * @param event breakEvent to be inserted
     */
    public void addBreak ( BreakEvent event ) {
        this.breaks.add( event );
    }

    /**
     * Removes a user's breakEvent from the breakEvents list
     *
     * @param id identifier of the break event to be removed
     */
    public void removeBreakEvent ( long id ) {
        this.breaks.removeIf( event -> event.getId() == id );
    }

    public List < Event > getEvents () {
        return Collections.unmodifiableList( events );
    }

    public void setEvents ( List < Event > events ) {
        this.events = events;
    }

    /**
     * Adds in the user's events list a new event
     *
     * @param event instance of the event to be inserted
     */
    public void addEvent ( Event event ) {
        this.events.add( event );
    }

    /**
     * Removes a user's event from the events list
     *
     * @param id identifier of the event to be removed
     */
    public void removeEvent ( long id ) {
        this.events.removeIf( event -> event.getId() == id );
    }

    public List < Ticket > getHeldTickets () {
        return Collections.unmodifiableList( heldTickets );
    }

    public void setHeldTickets ( List < Ticket > heldTickets ) {
        this.heldTickets = heldTickets;
    }

    /**
     * Adds in the user's tickets list a new ticket
     *
     * @param ticket instance of the ticket to be inserted
     */
    public void addTicket ( Ticket ticket ) {
        this.heldTickets.add( ticket );
    }


    /**
     * Removes a user's ticket from the tickets list
     *
     * @param id identifier of the ticket to be removed
     */
    public void deleteTicket ( long id ) {
        this.heldTickets.removeIf( ticket -> ticket.getId() == id );
    }

    public List < TypeOfEvent > getPreferences () {
        return Collections.unmodifiableList( preferences );
    }

    public void setPreferences ( List < TypeOfEvent > preferences ) {
        this.preferences = preferences;
    }

    /**
     * Adds in the user's preferences list a new preference
     *
     * @param preference instance of the preference to be inserted
     */
    public void addPreference ( TypeOfEvent preference ) {
        this.preferences.add( preference );
    }

    /**
     * Removes a user's preference from the preferences list
     *
     * @param id identifier of the preference to be removed
     */
    public void removePreference ( long id ) {
        preferences.removeIf( typeOfEvent -> typeOfEvent.getId() == id );
    }

    public List < UserLocation > getPreferredLocations () {
        return Collections.unmodifiableList( preferredLocations );
    }

    public void setPreferredLocations ( List < UserLocation > preferredLocations ) {
        this.preferredLocations = preferredLocations;
    }

    /**
     * Adds in the user's preferredLocations list a new location
     *
     * @param name identifier of the new preferred location
     * @param location new preferred location
     */
    public void addLocation ( String name, Location location ) {
        this.preferredLocations.add( new UserLocation( name, location ) );
    }

    /**
     * Removes a user's preferredLocation from the preferredLocations list
     *
     * @param name identifier of the location to be removed
     */
    public void removeLocation ( String name ) {
        preferredLocations.removeIf( userLocation -> userLocation.getName().equals( name ) );
    }

    /**
     * Checks if an user tuple is already present in the database
     *
     * @return true if present, false otherwise
     */
    @Override
    public boolean isAlreadyInDb () {
        try {
            load( email );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}