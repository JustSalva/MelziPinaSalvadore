package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;

@Entity( name = "USER_LOCATIONS" )
public class UserLocation extends GenericEntity {

    private static final long serialVersionUID = -6196594264279775552L;

    @Id
    private String name;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumns( {
            @JoinColumn( name = "LOCATION_LATITUDE", referencedColumnName = "latitude" ),
            @JoinColumn( name = "LOCATION_LONGITUDE", referencedColumnName = "longitude" )
    } )
    private Location location;

    public UserLocation() {
    }

    public UserLocation( String name, Location location ) {
        this.name = name;
        this.location = location;
    }

    public static UserLocation load( String key ) throws EntityNotFoundException {
        return GenericEntity.load( UserLocation.class, key );
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation( Location location ) {
        this.location = location;
    }

    @Override
    public boolean isAlreadyInDb() {
        try {
            load( name );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}
