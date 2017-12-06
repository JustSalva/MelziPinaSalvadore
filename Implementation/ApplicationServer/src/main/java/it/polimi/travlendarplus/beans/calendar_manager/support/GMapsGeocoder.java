package it.polimi.travlendarplus.beans.calendar_manager.support;

import it.polimi.travlendarplus.entities.Location;
import org.json.JSONArray;
import org.json.JSONObject;

public class GMapsGeocoder {

    public static Location getLocationObject (double lat, double lng) {
        return new Location (lat, lng, getLatLngAddress(lat, lng));
    }

    public static String getLatLngAddress(double lat, double lng) {
        String call = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng+
                "&key=AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y";
        JSONObject response = HTMLCallAndResponse.performCall(call);
        return response.getJSONArray("results").getJSONObject(0).
                getString("formatted_address");
    }
}
