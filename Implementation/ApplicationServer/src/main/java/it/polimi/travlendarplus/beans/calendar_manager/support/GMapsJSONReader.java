package it.polimi.travlendarplus.beans.calendar_manager.support;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.travelMeans.PrivateTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;

public class GMapsJSONReader {

    //it creates a Travel with only one TravelComponent, related to the NO_TRANSIT and NO_SHARING TravelMean used
    public ArrayList<Travel> getTravelNoTransitMeans(JSONObject response, TravelMeanEnum type, long departureTime,
                                         Location depLoc, Location arrLoc) {
        ArrayList<Travel> possiblePaths = new ArrayList<Travel>();
        JSONArray routes = getRoutes(response);

        for(int i=0; i<routes.length(); i++) {
            Instant startingTime = Instant.ofEpochSecond(departureTime);
            Instant endingTime = Instant.ofEpochSecond(departureTime + getTotDurationInSeconds(routes.getJSONObject(i)));
            float lengthInKm = ((float) getTotDistanceInMeters(routes.getJSONObject(i))) / 1000;
            PrivateTravelMean mean = new PrivateTravelMean(type.toString(), type, 0);

            TravelComponent component = new TravelComponent(startingTime, endingTime, lengthInKm, depLoc, arrLoc, mean);
            ArrayList<TravelComponent> listTC = new ArrayList<TravelComponent>();
            listTC.add(component);

            Travel travel = new Travel(null, listTC);
            possiblePaths.add(travel);
        }

        return possiblePaths;
    }

    public ArrayList<Travel> getTravelWithTransitMeans(JSONObject response) {
        ArrayList<Travel> possiblePaths = new ArrayList<Travel>();
        JSONArray routes = getRoutes(response);

        for(int i=0; i<routes.length(); i++) {
            JSONArray steps = getSteps(routes.getJSONObject(i));
            ArrayList<TravelComponent> travelSteps = new ArrayList<TravelComponent>();

            for(int j=0; j<steps.length(); j++) {
                Instant startingTime = Instant.ofEpochSecond(getSingleDepartureTimeInUnix(steps.getJSONObject(j)));
                Instant endingTime = Instant.ofEpochSecond(getSingleArrivalTimeInUnix(steps.getJSONObject(j)));
                float lengthInKm = ((float)getSingleDistanceInMeters(steps.getJSONObject(j))) / 1000;
                Location departureLoc;
                Location arrivalLoc;
                PublicTravelMean mean;

                //starting time, ending time, length, departure location, arrival location, travel mean

                TravelComponent step = null;
                travelSteps.add(step);
            }
            Travel travelOption = null;
            possiblePaths.add(travelOption);
        }

        return possiblePaths;
    }

    public static String getStatus(JSONObject response) {
        return response.getString("status");
    }

    private static JSONArray getRoutes(JSONObject response) {
        return response.getJSONArray("routes");
    }

    //for the use in T+ we have only an element in legs[]
    private static JSONObject getLegs(JSONObject singleRoute) {
        return singleRoute.getJSONArray("legs").getJSONObject(0);
    }

    private static String getOverviewPoliline(JSONObject singleRoute) {
        return singleRoute.getString("overview_polyline");
    }

    private static JSONObject getBounds(JSONObject singleRoute) {
        return singleRoute.getJSONObject("bounds");
    }

    private static JSONArray getSteps(JSONObject singleRoute) {
        return getLegs(singleRoute).getJSONArray("steps");
    }

    private static int getTotDistanceInMeters(JSONObject singleRoute) {
        return getLegs(singleRoute).getJSONObject("distance").getInt("value");
    }

    private static int getTotDurationInSeconds(JSONObject singleRoute) {
        return getLegs(singleRoute).getJSONObject("duration").getInt("value");
    }

    private static int getDurationInTrafficInSeconds(JSONObject singleRoute) {
        return getLegs(singleRoute).getJSONObject("duration_in_traffic").getInt("value");
    }

    private static int getSingleDistanceInMeters(JSONObject singleStep) {
        return singleStep.getJSONObject("distance").getInt("value");
    }

    private static JSONObject getTransitDetails(JSONObject singleStep) {
        JSONObject prova = null;
        try {
            prova = singleStep.getJSONObject("transit_details");
        } catch (JSONException e) {

        }
        return prova;
    }

    private static Location getSingleArrivalStop(JSONObject singleStep) {
        JSONObject loc = getTransitDetails(singleStep).getJSONObject("arrival_stop").getJSONObject("location");
        return GMapsGeocoder.getLocationObject(loc.getDouble("lat"), loc.getDouble("lng"));
    }

    private static Location getSingleDepartureStop(JSONObject singleStep) {
        JSONObject loc = getTransitDetails(singleStep).getJSONObject("departure_stop").getJSONObject("location");
        return GMapsGeocoder.getLocationObject(loc.getDouble("lat"), loc.getDouble("lng"));
    }

    private static long getSingleArrivalTimeInUnix(JSONObject singleStep){
        return getTransitDetails(singleStep).getJSONObject("arrival_time").getLong("value");
    }

    private static long getSingleDepartureTimeInUnix(JSONObject singleStep){
        return getTransitDetails(singleStep).getJSONObject("departure_time").getLong("value");
    }

    public static String getSingleLineName(JSONObject singleStep) {
        return getTransitDetails(singleStep).getJSONObject("line").getString("name");
    }


}
