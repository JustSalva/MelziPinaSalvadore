package it.polimi.travlendarplus.retrofit.body;


/**
 * Body to be sent to server to add a location.
 */
public class LocationBody {

    private String name;
    private String address;
    private String latitude;
    private String longitude;

    public LocationBody ( String name, String address, String latitude, String longitude ) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
