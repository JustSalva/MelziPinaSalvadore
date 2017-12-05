package it.polimi.travlendarplus.beans.calendar_manager.support;

import org.json.JSONObject;

public class GMapsGeocoder {

    public static String getCallReverseGeocoding(double lat, double lng) {
        return "https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng+
                "&key=AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y";
    }

    public static String getLatLngAddress(JSONObject responseReverseGeocoding) {
        return responseReverseGeocoding.getJSONArray("results").getJSONObject(0).
                getString("formatted_address");
    }
}
