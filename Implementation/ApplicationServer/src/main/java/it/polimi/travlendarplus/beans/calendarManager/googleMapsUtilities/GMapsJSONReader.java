package it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.travelMeans.PrivateTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.BadRequestException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsUnavailableException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.LocationNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;

/**
 * Helper class that is to be used to read the Google Maps direction responses
 */
public class GMapsJSONReader {

    public static String getStatus ( JSONObject response ) {
        return response.getString( "status" );
    }

    private static JSONArray getRoutes ( JSONObject response ) {
        return response.getJSONArray( "routes" );
    }

    //for the use in T+ we have only an element in legs[]
    private static JSONObject getLegs ( JSONObject singleRoute ) {
        return singleRoute.getJSONArray( "legs" ).getJSONObject( 0 );
    }

    private static String getOverviewPoliLine ( JSONObject singleRoute ) {
        return singleRoute.getString( "overview_polyline" );
    }

    private static JSONObject getBounds ( JSONObject singleRoute ) {
        return singleRoute.getJSONObject( "bounds" );
    }

    private static JSONArray getSteps ( JSONObject singleRoute ) {
        return getLegs( singleRoute ).getJSONArray( "steps" );
    }

    private static int getTotDistanceInMeters ( JSONObject singleRoute ) {
        return getLegs( singleRoute ).getJSONObject( "distance" ).getInt( "value" );
    }

    private static int getTotDurationInSeconds ( JSONObject singleRoute ) {
        return getLegs( singleRoute ).getJSONObject( "duration" ).getInt( "value" );
    }

    private static int getDurationInTrafficInSeconds ( JSONObject singleRoute ) {
        return getLegs( singleRoute ).getJSONObject( "duration_in_traffic" ).getInt( "value" );
    }

    private static int getSingleDistanceInMeters ( JSONObject singleStep ) {
        return singleStep.getJSONObject( "distance" ).getInt( "value" );
    }


    /**
     * This method is to be called in case of TRANSIT call to obtain detailed
     * info about the travel means to be taken.
     *
     * @param singleStep the travel to be analyzed
     * @return a Json object containing the requested details
     * @throws JSONException when no "transit_details" are given
     *                      ( this happen when there is a WALKING step )
     */
    private static JSONObject getTransitDetails ( JSONObject singleStep ) throws JSONException {
        return singleStep.getJSONObject( "transit_details" );
    }

    /**
     * Provide the ending location of a travel step
     *
     * @param singleStep the travel to be analyzed
     * @return a Json object containing the requested details
     * @throws GMapsUnavailableException if Google maps services are unavailable
     */
    private static Location getSingleArrivalStop ( JSONObject singleStep ) throws GMapsUnavailableException {
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails( singleStep );
        } catch ( JSONException e ) {
            JSONObject arrLoc = singleStep.getJSONObject( "end_location" );
            return GMapsGeocoder.getLocationObject( arrLoc.getDouble( "lat" ),
                    arrLoc.getDouble( "lng" ) );
        }
        JSONObject forName = transitDetails.getJSONObject( "arrival_stop" );
        JSONObject loc = forName.getJSONObject( "location" );
        return new Location( loc.getDouble( "lat" ), loc.getDouble( "lng" ),
                forName.getString( "name" ) );
    }

    /**
     * Provide the ending location of a travel step
     *
     * @param singleStep the travel to be analyzed
     * @return a Json object containing the requested details
     * @throws GMapsUnavailableException if Google maps services are unavailable
     */
    private static Location getSingleDepartureStop ( JSONObject singleStep ) throws GMapsUnavailableException {
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails( singleStep );
        } catch ( JSONException e ) {
            JSONObject arrLoc = singleStep.getJSONObject( "start_location" );
            return GMapsGeocoder.getLocationObject( arrLoc.getDouble( "lat" ),
                    arrLoc.getDouble( "lng" ) );
        }
        JSONObject forName = transitDetails.getJSONObject( "departure_stop" );
        JSONObject loc = forName.getJSONObject( "location" );
        return new Location( loc.getDouble( "lat" ), loc.getDouble( "lng" ),
                forName.getString( "name" ) );
    }

    // In all the functions below that take parameters for create TravelComponent, when JSONException happens
    // due to getTransitDetails() function, the parameters are taken from a different position in JSONObject.
    // The only difference is that departure time is setted to 0 and arrival time contains the duration in time!!!

    /**
     * Provide the arrival time of a single travel
     *
     * @param singleStep the travel to be analyzed
     * @return a Json object containing the requested details
     */
    private static long getSingleArrivalTimeInUnix ( JSONObject singleStep ) {
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails( singleStep );
        } catch ( JSONException e ) {
            return singleStep.getJSONObject( "duration" ).getLong( "value" );
        }
        return transitDetails.getJSONObject( "arrival_time" ).getLong( "value" );
    }

    /**
     * Provide the departure time of a single travel
     *
     * @param singleStep the travel to be analyzed
     * @return a Json object containing the requested details
     */
    private static long getSingleDepartureTimeInUnix ( JSONObject singleStep ) {
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails( singleStep );
        } catch ( JSONException e ) {
            return 0;
        }
        return transitDetails.getJSONObject( "departure_time" ).getLong( "value" );
    }

    /**
     * Provide the name of the public transportation service line
     * related to the specified travel
     *
     * @param singleStep the travel to be analyzed
     * @return a Json object containing the requested details
     */
    private static String getSingleLineName ( JSONObject singleStep ) {
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails( singleStep );
        } catch ( JSONException e ) {
            return singleStep.getString( "html_instructions" );
        }
        try {
            return transitDetails.getJSONObject( "line" ).getString( "name" );
        } catch ( JSONException e ) {
            return "";
        }
    }

    /**
     * Retrieve the name of the vehicle used in a specified travel
     *
     * @param singleStep the travel to be analyzed
     * @return a Json object containing the requested details
     */
    private static String getSingleVehicleType ( JSONObject singleStep ) {
        JSONObject transitDetails;
        try {
            transitDetails = getTransitDetails( singleStep );
        } catch ( JSONException e ) {
            return "WALKING";
        }
        try {
            return transitDetails.getJSONObject( "line" ).getJSONObject( "vehicle" ).getString( "type" );
        } catch ( JSONException e ) {
            return "";
        }
    }

    /**
     * Given a String that represent a vehicle it returns his relative category
     *
     * @param vehicleType string that describe the vehicle to be converted
     * @return the relative travelMean enum type
     */
    private static TravelMeanEnum getProperTravelMeanEnum ( String vehicleType ) {
        switch ( vehicleType ) {
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

    /**
     * It creates a Travel with only one TravelComponent, related to NO_TRANSIT
     * and NO_SHARING TravelMean of the specified type.
     *
     * @param response Json returned by Google Maps APIs as response
     * @param type category of the travel mean that is connected to a travel
     * @param travelTime travel duration
     * @param departure is used to specify if travelTime refers to departure:
     *                  if FALSE it refers to arrival.
     * @param depLoc location at which the travel starts
     * @param arrLoc location at which the travel ends
     * @return a list of feasible travels
     * @throws LocationNotFoundException if google APIs signals that an inserted location is invalid
     * @throws GMapsUnavailableException if google APIs services are not available
     * @throws BadRequestException if the request sent contains syntactical errors
     */
    public ArrayList < Travel > getTravelNoTransitMeans ( JSONObject response, TravelMeanEnum type,
                                                          long travelTime, boolean departure,
                                                          Location depLoc, Location arrLoc )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {

        ArrayList < Travel > possiblePaths = new ArrayList < Travel >();

        checkErrorInStatusCode( response ); //throws an exception if the response is different from OK

        JSONArray routes = getRoutes( response );

        for ( int i = 0; i < routes.length(); i++ ) {
            Instant startingTime, endingTime;
            if ( departure ) {
                startingTime = Instant.ofEpochSecond( travelTime );
                endingTime = Instant.ofEpochSecond( travelTime +
                        getTotDurationInSeconds( routes.getJSONObject( i ) ) );
            } else {
                startingTime = Instant.ofEpochSecond( travelTime -
                        getTotDurationInSeconds( routes.getJSONObject( i ) ) );
                endingTime = Instant.ofEpochSecond( travelTime );
            }
            float lengthInKm = ( ( float ) getTotDistanceInMeters( routes.getJSONObject( i ) ) ) / 1000;
            PrivateTravelMean mean = new PrivateTravelMean( type.toString(), type, 0 );

            TravelComponent component = new TravelComponent( startingTime, endingTime,
                    lengthInKm, depLoc, arrLoc, mean );
            ArrayList < TravelComponent > listTC = new ArrayList < TravelComponent >();
            listTC.add( component );

            Travel travel = new Travel( listTC );
            possiblePaths.add( travel );
        }

        return possiblePaths;

    }

    /**
     * It creates a Travel composed by one or more components, related to TRANSIT TravelMeans
     *
     * @param response Json returned by Google Maps APIs as response
     * @return a list of feasible travels
     * @throws LocationNotFoundException if google APIs signals that an inserted location is invalid
     * @throws GMapsUnavailableException if google APIs services are not available
     * @throws BadRequestException if the request sent contains syntactical errors
     */
    public ArrayList < Travel > getTravelWithTransitMeans ( JSONObject response )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException  {
        ArrayList < Travel > possiblePaths = new ArrayList < Travel >();

        checkErrorInStatusCode( response ); //throws an exception if the response is different from OK

        JSONArray routes = getRoutes( response );

        for ( int i = 0; i < routes.length(); i++ ) {
            JSONArray steps = getSteps( routes.getJSONObject( i ) );
            ArrayList < TravelComponent > travelSteps = new ArrayList < TravelComponent >();

            for ( int j = 0; j < steps.length(); j++ ) {
                Instant startingTime = Instant.ofEpochSecond(
                        getSingleDepartureTimeInUnix( steps.getJSONObject( j ) ) );
                Instant endingTime = Instant.ofEpochSecond(
                        getSingleArrivalTimeInUnix( steps.getJSONObject( j ) ) );
                float lengthInKm = ( ( float ) getSingleDistanceInMeters( steps.getJSONObject( j ) ) ) / 1000;
                Location departureLoc = getSingleDepartureStop( steps.getJSONObject( j ) );
                departureLoc.save();
                Location arrivalLoc = getSingleArrivalStop( steps.getJSONObject( j ) );
                arrivalLoc.save();
                TravelMeanEnum meanEnum = getProperTravelMeanEnum(
                        getSingleVehicleType( steps.getJSONObject( j ) ) );
                PublicTravelMean mean = new PublicTravelMean(
                        getSingleLineName( steps.getJSONObject( j ) ), meanEnum, 0 );

                TravelComponent step = new TravelComponent( startingTime, endingTime, lengthInKm, departureLoc,
                        arrivalLoc, mean );
                travelSteps.add( step );
            }
            Travel travelOption = new Travel( travelSteps );
            possiblePaths.add( travelOption );
        }
        return possiblePaths;
    }

    /**
     * Checks a google response message; this method is to be invoked when
     * the response status code, returned by google APIs, have to be checked,
     * an relative exception is thrown if in the status code is signaled
     * that something goes wrong during the HTTP call
     *
     * @param response JSONObject containing the response returned by google APIs
     * @throws LocationNotFoundException if google APIs signals that an inserted location is invalid
     * @throws GMapsUnavailableException if google APIs services are not available
     * @throws BadRequestException if the request sent contains syntactical errors
     */
    private void checkErrorInStatusCode ( JSONObject response )
            throws LocationNotFoundException, GMapsUnavailableException, BadRequestException {
        switch ( getStatus( response ) ) {
            case "OK":
            case "ZERO_RESULTS":
                break;
            case "NOT_FOUND":
                throw new LocationNotFoundException();
            case "REQUEST_DENIED":
            case "UNKNOWN_ERROR":
            case "OVER_QUERY_LIMIT":
                throw new GMapsUnavailableException();
            default:
                throw new BadRequestException();
        }
    }

}
