package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * This JPA class represent a location ( geographic coordinates of a specific point on the earth surface)
 */
@Entity( name = "LOCATION" )
@IdClass( LocationId.class )
public class Location extends GenericEntity {

    private static final long serialVersionUID = -8275362646231911325L;

    @Id
    private double latitude;
    @Id
    private double longitude;

    /**
     * Specific address relative to the specified geographic coordinates,
     * Addresses are written in order from most specific to general,
     * i.e. finest to coarsest information, starting with the addressee
     * and ending with the largest geographical unit.
     * (ex.Name - Company name - Street - City area/District - City/Town/Village - County - Postal code - Country )
     */
    @Column( name = "ADDRESS" )
    private String address;

    public Location () {
    }

    public Location ( double latitude, double longitude ) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location ( double latitude, double longitude, String address ) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    /**
     * Allows to load a Location class from the database
     *
     * @param key primary key of the location tuple
     * @return the requested tuple as a Location class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static Location load ( LocationId key ) throws EntityNotFoundException {
        return GenericEntity.load( Location.class, key );
    }

    public double getLatitude () {
        return latitude;
    }

    public void setLatitude ( double latitude ) {
        this.latitude = latitude;
    }

    public double getLongitude () {
        return longitude;
    }

    public void setLongitude ( double longitude ) {
        this.longitude = longitude;
    }

    public String getAddress () {
        return address;
    }

    public void setAddress ( String address ) {
        this.address = address;
    }

    @Override
    public String toString () {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                '}';
    }

    /**
     * Checks if a location instance is already present in the database
     *
     * @return true if present, false otherwise
     */
    @Override
    public boolean isAlreadyInDb () {
        try {
            load( new LocationId( latitude, longitude ) );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}

