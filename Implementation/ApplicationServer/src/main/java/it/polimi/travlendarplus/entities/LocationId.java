package it.polimi.travlendarplus.entities;

import java.io.Serializable;

/**
 * Support class used to provide a key to Location class, since JPA needs
 * that a key is represented either by a primitive data type or by a class
 *
 * @see Location
 */
public class LocationId implements Serializable {

    private static final long serialVersionUID = -6220268034665635816L;

    private double latitude;
    private double longitude;

    public LocationId ( double latitude, double longitude ) {
        this.latitude = latitude;
        this.longitude = longitude;
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
}
