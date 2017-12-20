package it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsUnavailableException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GMapsGeocoder {

    public static Location getLocationObject ( double lat, double lng ) throws GMapsUnavailableException {
        return new Location( lat, lng, getLatLngAddress( lat, lng ) );
    }

    //TODO check if it is the best option
    public static String getLatLngAddress ( double lat, double lng ) throws GMapsUnavailableException {
        String call = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng +
                "&key=AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y";
        try {
            JSONObject response = HTMLCallAndResponse.performCall( call );
            return response.getJSONArray( "results" ).getJSONObject( 0 ).
                    getString( "formatted_address" );
        } catch ( JSONException e ) {
            Logger.getLogger( GMapsGeocoder.class.getName() ).log( Level.SEVERE, e.getMessage(), e );
            return "";
        }
    }

    public static Location getLocationByString ( String address ) throws GMapsUnavailableException {
        address = address.replace( " ", "+" );
        Location retLoc = new Location();
        String call = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address +
                "&key=AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y";
        try {
            JSONObject response = HTMLCallAndResponse.performCall( call );
            JSONObject obj = response.getJSONArray( "results" ).getJSONObject( 0 );
            retLoc.setAddress( obj.getString( "formatted_address" ) );
            JSONObject geometry = obj.getJSONObject( "geometry" );
            JSONObject location = geometry.getJSONObject( "location" );
            retLoc.setLatitude( location.getDouble( "lat" ) );
            retLoc.setLongitude( location.getDouble( "lng" ) );
        } catch ( JSONException e ) {
            Logger.getLogger( GMapsGeocoder.class.getName() ).log( Level.SEVERE, e.getMessage(), e );
        }
        return retLoc;
    }
}
