package it.polimi.travlendarplus.retrofit.response;


/**
 * Location parsed by the JSON returned by the server.
 */
public class GetLocationsResponse {

    private String name;
    private Position location;

    public String getName () {
        return name;
    }

    public Position getLocation () {
        return location;
    }

    private class Position {
        private float latitude;
        private float longitude;
        private String address;

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
}
