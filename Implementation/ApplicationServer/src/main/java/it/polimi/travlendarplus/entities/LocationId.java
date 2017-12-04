package it.polimi.travlendarplus.entities;

import java.io.Serializable;

public class LocationId implements Serializable {

    private double latitude;
    private double longitude;

    public LocationId( double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
