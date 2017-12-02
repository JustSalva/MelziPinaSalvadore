package it.polimi.travlendarplus.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity(name = "LOCATION")
@IdClass(LocationID.class)
public class Location implements Serializable {

    @Id
    private double latitude;
    @Id
    private double longitude;

    @Column(name = "ADDRESS")
    private String address;

    public Location() {
    }

    public Location(float latitude, float longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
class LocationID implements Serializable{
    private double latitude;
    private double longitude;
}
