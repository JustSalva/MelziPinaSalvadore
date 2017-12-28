package it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities;

import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

import java.util.List;

public class GMapsDirectionsHandler {
    private StringBuilder callURL;

    public GMapsDirectionsHandler () {
        callURL = new StringBuilder( "https://maps.googleapis.com/maps/api/directions/json?" );
        baseCall();
    }

    public GMapsDirectionsHandler ( String callURL ) {
        this.callURL = new StringBuilder( callURL );
    }

    public String getCallURL () {
        return callURL.toString();
    }

    /*
     * getBaseCallPreviousPath() & getBaseCallFollowingPath() contain parameters for origin, destination and
     * departure_time. They return a String that can be enriched with other params using the other functions defined
     * in this class.
     */

    /**
     * Builds the url used to calculate eventual previous paths related to a specified event:
     * the function requires also the previous event.
     *
     * @param event           event of which calculate previous path.
     * @param previousEvent   previous event.
     * @param prevDateAllowed boolean that indicates if the ending time of previous event is in the past.
     * @return the base url for a generic calculation of previous paths.
     */
    public String getBaseCallPreviousPath ( Event event, Event previousEvent, boolean prevDateAllowed ) {
        addParam( "origin", event.getDeparture().getLatitude() + "," +
                event.getDeparture().getLongitude() );
        addParam( "destination", event.getEventLocation().getLatitude() + "," +
                event.getEventLocation().getLongitude() );
        if ( previousEvent == null || event.isTravelAtLastChoice() || !prevDateAllowed ) {
            addParam( "arrival_time", event.getStartingTime().getEpochSecond() + "" );
        } else {
            addParam( "departure_time", previousEvent.getEndingTime().getEpochSecond() + "" );
        }
        return callURL.toString();
    }

    /**
     * Builds the url used to calculate eventual following paths related to a specified event:
     * the function requires also the following event.
     *
     * @param event           event of which calculate previous path.
     * @param followingEvent  following event.
     * @param prevDateAllowed boolean that indicates if the ending time of the event is in the past.
     * @return the base url for a generic calculation of following paths.
     */
    public String getBaseCallFollowingPath ( Event event, Event followingEvent, boolean prevDateAllowed ) {
        addParam( "origin", followingEvent.getDeparture().getLatitude() + "," +
                followingEvent.getDeparture().getLongitude() );
        addParam( "destination", followingEvent.getEventLocation().getLatitude() + "," +
                followingEvent.getEventLocation().getLongitude() );
        if ( followingEvent.isTravelAtLastChoice() || !prevDateAllowed ) {
            addParam( "arrival_time", followingEvent.getStartingTime().getEpochSecond() + "" );
        } else {
            addParam( "departure_time", event.getEndingTime().getEpochSecond() + "" );
        }
        return callURL.toString();
    }

    /**
     * Concatenates the private travel mean required in the call to google mpas APIs.
     *
     * @param tempCall base call obtained from other functions of the class
     * @param type     key word to specify the private mean to use for the path calculation.
     * @return a url ready for the request to google maps with a specified private travel mean.
     */
    public String getCallWithNoTransit ( String tempCall, TravelMeanEnum type ) {
        return tempCall + "&mode=" + type.getParam() + "&alternatives=true";
    }

    /**
     * Concatenates all the public travel means required in the call to google mpas APIs.
     *
     * @param tempCall     base call obtained from other functions of the class
     * @param transitMeans a list that specify all the public means to use for the path calculation.
     * @return a url ready for the request to google maps with the specified public travel means.
     */
    public String getCallByTransit ( String tempCall, List < TravelMeanEnum > transitMeans ) {
        StringBuilder callWithTravel = new StringBuilder( tempCall );
        if ( transitMeans.size() > 0 ) {
            callWithTravel.append( "&mode=transit" );
            callWithTravel.append( "&transit_mode=" + transitMeans.get( 0 ).getParam() );
            for ( int i = 1; i < transitMeans.size(); i++ ) {
                callWithTravel.append( "|" + transitMeans.get( i ).getParam() );
            }
        }
        if ( transitMeans.size() > 1 ) {
            callWithTravel.append( "&alternatives=true" );
        }
        return callWithTravel.toString();
    }

    /**
     * Concatenates a parameter to the url.
     *
     * @param param   parameter to concatenate in the URL.
     * @param address value of the parameter added.
     */
    private void addParam ( String param, String address ) {
        if ( callURL.charAt( getCallURL().length() - 1 ) != '?' ) {
            callURL.append( "&" );
        }
        callURL.append( param ).append( "=" ).append( address );
    }

    /**
     * Creates the base call that is used in any direction call made by Travlendar+.
     */
    private void baseCall () {
        addParam( "key", "AIzaSyBNIQeUu-paVMsVbybXDK8fEfg0RL7uZgo" );
        addParam( "units", "metric" );
        addParam( "avoid", "ferries" );
    }

}
