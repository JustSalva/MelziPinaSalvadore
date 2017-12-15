package it.polimi.travlendarplus.beans.calendar_manager.support;

import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsException.BadRequestException;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsException.GMapsGeneralException;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsException.LocationNotFoundException;
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

    // It creates a Travel with only one TravelComponent, related to NO_TRANSIT and NO_SHARING TravelMean of the specified type.
    // Boolean departure is used to specify if travelTime refers to departure: if FALSE it refers to arrival.
    public ArrayList<Travel> getTravelNoTransitMeans(JSONObject response, TravelMeanEnum type, long travelTime, boolean departure,
                                         Location depLoc, Location arrLoc) throws GMapsGeneralException{
        ArrayList<Travel> possiblePaths = new ArrayList<Travel>();

        if(getStatus(response).equals("OK") || getStatus(response).equals("ZERO_RESULTS")) {
            JSONArray routes = getRoutes(response);

            for (int i = 0; i < routes.length(); i++) {
                Instant startingTime, endingTime;
                if(departure) {
                    startingTime = Instant.ofEpochSecond(travelTime);
                    endingTime = Instant.ofEpochSecond(travelTime + getTotDurationInSeconds(routes.getJSONObject(i)));
                } else {
                    startingTime = Instant.ofEpochSecond(travelTime - getTotDurationInSeconds(routes.getJSONObject(i)));
                    endingTime = Instant.ofEpochSecond(travelTime);
                }
                float lengthInKm = ((float) getTotDistanceInMeters(routes.getJSONObject(i))) / 1000;
                PrivateTravelMean mean = new PrivateTravelMean(type.toString(), type, 0);

                TravelComponent component = new TravelComponent(startingTime, endingTime, lengthInKm, depLoc, arrLoc, mean);
                ArrayList<TravelComponent> listTC = new ArrayList<TravelComponent>();
                listTC.add(component);

                Travel travel = new Travel( listTC );
                possiblePaths.add(travel);
            }
            return possiblePaths;
        }

        else if (getStatus(response).equals("NOT_FOUND"))
            throw new LocationNotFoundException();

        else
            throw new BadRequestException();

    }

    //it creates a Travel composed by one or more components, related to TRANSIT TravelMeans
    public ArrayList<Travel> getTravelWithTransitMeans(JSONObject response) throws GMapsGeneralException{
        ArrayList<Travel> possiblePaths = new ArrayList<Travel>();

        if(getStatus(response).equals("OK") || getStatus(response).equals("ZERO_RESULTS")) {
            JSONArray routes = getRoutes(response);

            for (int i = 0; i < routes.length(); i++) {
                JSONArray steps = getSteps(routes.getJSONObject(i));
                ArrayList<TravelComponent> travelSteps = new ArrayList<TravelComponent>();

                for (int j = 0; j < steps.length(); j++) {
                    Instant startingTime = Instant.ofEpochSecond(getSingleDepartureTimeInUnix(steps.getJSONObject(j)));
                    Instant endingTime = Instant.ofEpochSecond(getSingleArrivalTimeInUnix(steps.getJSONObject(j)));
                    float lengthInKm = ((float) getSingleDistanceInMeters(steps.getJSONObject(j))) / 1000;
                    Location departureLoc = getSingleDepartureStop(steps.getJSONObject(j));
                    Location arrivalLoc = getSingleArrivalStop(steps.getJSONObject(j));
                    TravelMeanEnum meanEnum = getProperTravelMeanEnum(getSingleVehicleType(steps.getJSONObject(j)));
                    PublicTravelMean mean = new PublicTravelMean(getSingleLineName(steps.getJSONObject(j)), meanEnum, 0);

                    TravelComponent step = new TravelComponent(startingTime, endingTime, lengthInKm, departureLoc,
                            arrivalLoc, mean);
                    travelSteps.add(step);
                }
                Travel travelOption = new Travel( travelSteps );
                possiblePaths.add(travelOption);
            }

            return possiblePaths;
        }

        else if (getStatus(response).equals("NOT_FOUND"))
            throw new LocationNotFoundException();

        else
            throw new BadRequestException();

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

    //getTransitDetails() is called in case of TRANSIT call.
    //The only case in which no "transit_details" are given is when there is a WALKING step
    //in this case a JSONException is thrown.
    private static JSONObject getTransitDetails(JSONObject singleStep) {
        return singleStep.getJSONObject("transit_details");
    }

    // In all the functions below that take parameters for create TravelComponent, when JSONException happens
    // due to getTransitDetails() function, the parameters are taken from a different position in JSONObject.
    // The only difference is that departure time is setted to 0 and arrival time contains the duration in time!!!

    private static Location getSingleArrivalStop(JSONObject singleStep) {
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails(singleStep);
        } catch (JSONException e) {
            JSONObject arrLoc = singleStep.getJSONObject("end_location");
            return GMapsGeocoder.getLocationObject(arrLoc.getDouble("lat"),
                    arrLoc.getDouble("lng"));
        }
        JSONObject forName = transitDetails.getJSONObject("arrival_stop");
        JSONObject loc = forName.getJSONObject("location");
        return new Location(loc.getDouble("lat"), loc.getDouble("lng"), forName.getString("name"));
    }

    private static Location getSingleDepartureStop(JSONObject singleStep) {
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails(singleStep);
        } catch (JSONException e) {
            JSONObject arrLoc = singleStep.getJSONObject("start_location");
            return GMapsGeocoder.getLocationObject(arrLoc.getDouble("lat"),
                    arrLoc.getDouble("lng"));
        }
        JSONObject forName = transitDetails.getJSONObject("departure_stop");
        JSONObject loc = forName.getJSONObject("location");
        return new Location(loc.getDouble("lat"), loc.getDouble("lng"), forName.getString("name"));
    }

    private static long getSingleArrivalTimeInUnix(JSONObject singleStep){
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails(singleStep);
        } catch (JSONException e) {
            return singleStep.getJSONObject("duration").getLong("value");
        }
        return transitDetails.getJSONObject("arrival_time").getLong("value");
    }

    private static long getSingleDepartureTimeInUnix(JSONObject singleStep){
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails(singleStep);
        } catch (JSONException e) {
            return 0;
        }
        return transitDetails.getJSONObject("departure_time").getLong("value");
    }

    private static String getSingleLineName(JSONObject singleStep) {
        JSONObject transitDetails;
        try{
            transitDetails = getTransitDetails(singleStep);
        } catch(JSONException e) {
            return singleStep.getString("html_instructions");
        }
        try {
            return transitDetails.getJSONObject("line").getString("name");
        } catch(JSONException e) {
            return "";
        }
    }

    private static String getSingleVehicleType(JSONObject singleStep) {
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails(singleStep);
        } catch (JSONException e) {
            return "WALKING";
        }
        return transitDetails.getJSONObject("line").getJSONObject("vehicle").getString("type");
    }

    private static TravelMeanEnum getProperTravelMeanEnum(String vehicleType) {
        switch(vehicleType) {
            case "RAIL":
            case "MONORAIL":
            case "HEAVY_RAIL":
            case "COMMUTER_TRAIN":
            case "HIGH_SPEED_TRAIN":
                return TravelMeanEnum.TRAIN;
            case "BUS":
            case "INTERCITY_BUS":
            case "TROLLEYBUS":
                return TravelMeanEnum.BUS;
            case "METRO_RAIL":
            case "SUBWAY":
                return TravelMeanEnum.SUBWAY;
            case "TRAM":
                return TravelMeanEnum.TRAM;
            case "WALKING":
                return TravelMeanEnum.BY_FOOT;
            default:
                return TravelMeanEnum.OTHER;
        }
    }

}
