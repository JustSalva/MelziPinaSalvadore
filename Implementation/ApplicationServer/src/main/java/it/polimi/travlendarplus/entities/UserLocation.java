package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;

/**
 * This JPA class represent a preferred user location
 */
@Entity( name = "USER_LOCATIONS" )
public class UserLocation extends GenericEntity {

    private static final long serialVersionUID = -6196594264279775552L;

    /**
     * Name of the preferred location, chosen by the user
     */
    @Id
    private String name;

    /**
     * Preferred location instance
     */
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumns( {
            @JoinColumn( name = "LOCATION_LATITUDE", referencedColumnName = "latitude" ),
            @JoinColumn( name = "LOCATION_LONGITUDE", referencedColumnName = "longitude" )
    } )
    private Location location;

    public UserLocation () {
    }

    public UserLocation ( String name, Location location ) {
        this.name = name;
        this.location = location;
    }

    /**
     * Allows to load a UserLocation class from the database
     *
     * @param key primary key of the userLocation tuple
     * @return the requested tuple as a UserLocation class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static UserLocation load ( String key ) throws EntityNotFoundException {
        return GenericEntity.load( UserLocation.class, key );
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public Location getLocation () {
        return location;
    }

    public void setLocation ( Location location ) {
        this.location = location;
    }

    /**
     * Checks if an userLocation is already present in the database
     *
     * @return true if present, false otherwise
     */
    @Override
    public boolean isAlreadyInDb () {
        try {
            load( name );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}
