package it.polimi.travlendarplus.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "LOCATION")
@IdClass(LocationId.class)
public class Location extends GeneralEntity implements Serializable{

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

    @Override
    public Location load(long key) throws EntityNotFoundException, NoResultException {
        return loadHelper(Location.class, key);
    }
}

