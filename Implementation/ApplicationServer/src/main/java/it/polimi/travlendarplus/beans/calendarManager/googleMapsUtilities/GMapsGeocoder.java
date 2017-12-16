package it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities;

import it.polimi.travlendarplus.entities.Location;
import org.json.JSONException;
import org.json.JSONObject;

public class GMapsGeocoder {

    public static Location getLocationObject ( double lat, double lng ) {
        return new Location( lat, lng, getLatLngAddress( lat, lng ) );
    }

    //TODO check if it is the best option
    public static String getLatLngAddress ( double lat, double lng ) {
        String call = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng +
                "&key=AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y";
        try {
            JSONObject response = HTMLCallAndResponse.performCall( call );
            return response.getJSONArray( "results" ).getJSONObject( 0 ).
                    getString( "formatted_address" );
        } catch ( JSONException e ) {
            return "";
        }
    }
}
