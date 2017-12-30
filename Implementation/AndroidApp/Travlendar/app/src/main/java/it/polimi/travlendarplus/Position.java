package it.polimi.travlendarplus;


/**
 * Contains info regarding a location's position.
 */
public class Position {
    private float latitude;
    private float longitude;
    private String address;

    public Position ( String address ) {
        this.address = address;
    }

    public float getLatitude () {
        return latitude;
    }

    public float getLongitude () {
        return longitude;
    }

    public String getAddress () {
        return address;
    }
}
