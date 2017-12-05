package it.polimi.travlendarplus.beans.calendar_manager.support;

import it.polimi.travlendarplus.entities.Location;
import org.json.JSONArray;
import org.json.JSONObject;

public class GMapsJSONReader {

    //TODO add static name to functions

    public String getStatus(JSONObject response) {
        return response.getString("status");
    }

    public JSONArray getRoutes(JSONObject response) {
        return response.getJSONArray("routes");
    }

    //for the use in T+ we have only an element in legs[]
    private JSONObject getLegs(JSONObject singleRoute) {
        return singleRoute.getJSONArray("legs").getJSONObject(0);
    }

    public String getOverviewPoliline(JSONObject singleRoute) {
        return singleRoute.getString("overview_polyline");
    }

    public JSONObject getBounds(JSONObject singleRoute) {
        return singleRoute.getJSONObject("bounds");
    }

    private JSONArray getSteps(JSONObject singleRoute) {
        return getLegs(singleRoute).getJSONArray("steps");
    }

    public int getTotDistanceInMeters(JSONObject singleRoute) {
        return getLegs(singleRoute).getJSONObject("distance").getInt("value");
    }

    public int getTotDurationInSeconds(JSONObject singleRoute) {
        return getLegs(singleRoute).getJSONObject("duration").getInt("value");
    }

    public int getDurationInTrafficInSeconds(JSONObject singleRoute) {
        return getLegs(singleRoute).getJSONObject("duration_in_traffic").getInt("value");
    }

    public int getSingleDistanceInMeters(JSONObject singleStep) {
        return singleStep.getJSONObject("distance").getInt("value");
    }

    private JSONObject getTransitDetails(JSONObject singleStep) {
        return singleStep.getJSONObject("transit_details");
    }

    public Location getSingleArrivalStop(JSONObject singleStep) {
        JSONObject loc = getTransitDetails(singleStep).getJSONObject("arrival_stop").getJSONObject("location");
        return createLocation(loc);
    }

    public Location getSingleDepartureStop(JSONObject singleStep) {
        JSONObject loc = getTransitDetails(singleStep).getJSONObject("departure_stop").getJSONObject("location");
        return createLocation(loc);
    }

    private Location createLocation (JSONObject loc) {
        double lat = loc.getDouble("lat");
        double lng = loc.getDouble("lng");
        JSONObject reverseGeocode = new JSONObject(GMapsGeocoder.getCallReverseGeocoding(lat, lng));
        return new Location(lat, lng, GMapsGeocoder.getLatLngAddress(reverseGeocode));
    }

    public long getSingleArrivalTimeInUnix(JSONObject singleStep){
        return getTransitDetails(singleStep).getJSONObject("arrival_time").getLong("value");
    }

    public long getSingleDepartureTimeInUnix(JSONObject singleStep){
        return getTransitDetails(singleStep).getJSONObject("departure_time").getLong("value");
    }

    public String getSingleLineName(JSONObject singleStep) {
        return getTransitDetails(singleStep).getJSONObject("line").getString("name");
    }


}
