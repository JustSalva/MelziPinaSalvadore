package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import org.json.JSONObject;

import javax.persistence.*;

@Entity(name = "LOCATION")
@IdClass(LocationId.class)
public class Location extends GenericEntity {

    private static final long serialVersionUID = -8275362646231911325L;

    @Id
    private double latitude;
    @Id
    private double longitude;

    @Column(name = "ADDRESS")
    private String address;

    public Location() {
    }

    public Location(double latitude, double longitude, String address) {
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

    public static Location load(LocationId key) throws EntityNotFoundException {
        return GenericEntity.load( Location.class, key );
    }

    @Override
    public boolean isAlreadyInDb() {
        try {
            load(new LocationId(latitude,longitude));
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}

