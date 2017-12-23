package it.polimi.travlendarplus.entities;

import java.io.Serializable;
import java.util.Objects;

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

    public LocationId () {
    }

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

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        LocationId that = ( LocationId ) o;
        return Double.compare( that.latitude, latitude ) == 0 &&
                Double.compare( that.longitude, longitude ) == 0;
    }

    @Override
    public int hashCode () {
        return Objects.hash( latitude, longitude );
    }
}
