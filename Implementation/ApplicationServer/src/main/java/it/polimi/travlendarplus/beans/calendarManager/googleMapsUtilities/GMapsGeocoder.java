package it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.BadRequestException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsUnavailableException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.LocationNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This helper class allows to perform requests Google GeoCoding APIS
 */
public class GMapsGeocoder {

    /**
     * Creates a Location object from a pair of coordinates.
     *
     * @param lat latitude of the location.
     * @param lng longitude of the location.
     * @return a location, according to the specified coordinates.
     * @throws GMapsUnavailableException if the path computation fails cause Google maps services are unavailable.
     * @throws BadRequestException       if the path computation fails cause a wrong request.
     * @throws LocationNotFoundException if the path computation fails cause the location specified is not found.
     */
    public static Location getLocationObject ( double lat, double lng )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        return new Location( lat, lng, getLatLngAddress( lat, lng ) );
    }


    /**
     * Creates the string url for geocode service, used to obtain the address of the specified location.
     *
     * @param lat latitude of the location.
     * @param lng longitude of the location.
     * @return the call for geocode service used to obtain the address of the specified location.
     * @throws GMapsUnavailableException if the path computation fails cause Google maps services are unavailable.
     * @throws BadRequestException       if the path computation fails cause a wrong request.
     * @throws LocationNotFoundException if the path computation fails cause the location specified is not found.
     */
    public static String getLatLngAddress ( double lat, double lng )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        String call = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng +
                "&key=AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y";

        try {
            JSONObject response = HTMLCallAndResponse.performCall( call );
            GMapsJSONReader.checkErrorInStatusCode( response );
            return response.getJSONArray( "results" ).getJSONObject( 0 ).
                    getString( "formatted_address" );
        } catch ( JSONException e ) {
            Logger.getLogger( GMapsGeocoder.class.getName() ).log( Level.SEVERE, e.getMessage(), e );
            return "";
        }
    }

    /**
     * Creates a Location object from an address string.
     *
     * @param address address of the location.
     * @return the Location object according to the address specified
     * @throws GMapsUnavailableException if the path computation fails cause Google maps services are unavailable.
     * @throws BadRequestException       if the path computation fails cause a wrong request.
     * @throws LocationNotFoundException if the path computation fails cause the location specified is not found.
     */
    public static Location getLocationByString ( String address )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        address = address.replace( " ", "+" );
        Location retLoc = new Location();
        String call = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address +
                "&AIzaSyBNIQeUu-paVMsVbybXDK8fEfg0RL7uZgo";
        try {
            JSONObject response = HTMLCallAndResponse.performCall( call );
            GMapsJSONReader.checkErrorInStatusCode( response );
            JSONObject obj = response.getJSONArray( "results" ).getJSONObject( 0 );
            retLoc.setAddress( obj.getString( "formatted_address" ) );
            JSONObject geometry = obj.getJSONObject( "geometry" );
            JSONObject location = geometry.getJSONObject( "location" );
            retLoc.setLatitude( location.getDouble( "lat" ) );
            retLoc.setLongitude( location.getDouble( "lng" ) );
        } catch ( JSONException e ) {
            Logger.getLogger( GMapsGeocoder.class.getName() ).log( Level.SEVERE, e.getMessage(), e );
            throw new GMapsUnavailableException();
        }
        return retLoc;
    }
}
