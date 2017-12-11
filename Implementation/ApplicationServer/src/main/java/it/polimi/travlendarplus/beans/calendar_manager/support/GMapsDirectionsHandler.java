package it.polimi.travlendarplus.beans.calendar_manager.support;

import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import java.util.List;

public class GMapsDirectionsHandler {
    private StringBuilder callURL;

    public GMapsDirectionsHandler() {
        callURL = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        baseCall();
    }

    //TODO handle the case in which the first of travel components is by feet (departure time set to 0)

    public GMapsDirectionsHandler(String callURL) {
        this.callURL = new StringBuilder(callURL);
    }

    public String getCallURL() {
        return callURL.toString();
    }

    // getBaseCallPreviousPath() & getBaseCallFollowingPath() contain parameters for origin, destination and
    // departure_time. They return a String that can be enriched with other params using the functions defined
    // in this class.

    //it calculates eventual paths related to this event: the function requires the previous event
    public String getBaseCallPreviousPath(Event event, Event previousEvent) {
        addParam("origin", event.getDeparture().getLatitude()+","+event.getDeparture().getLongitude());
        addParam("destination", event.getEventLocation().getLatitude()+","+
                event.getEventLocation().getLongitude());
        if (previousEvent != null)
            addParam("departure_time", previousEvent.getEndingTime().getEpochSecond()+"");
        else
            addParam("arrival_time", event.getStartingTime().getEpochSecond()+"");
        return callURL.toString();
    }

    //it calculates eventual paths after this event: the function requires the following event
    public String getBaseCallFollowingPath(Event event, Event followingEvent) {
        addParam("origin", event.getEventLocation().getLatitude()+","+
                event.getEventLocation().getLongitude());
        addParam("destination", followingEvent.getEventLocation().getLatitude()+","+
                followingEvent.getEventLocation().getLongitude());
        addParam("departure_time", event.getEndingTime().getEpochSecond()+"");
        return callURL.toString();
    }

    //it calculates eventual paths given a departure location, an arrival location and a departure time;
    //locations are specified with address
    public String getBaseCallLocationTime(double depLat, double depLng, double arrLat, double arrlLng, long departureTime) {
        addParam("origin", depLat+","+depLng);
        addParam("destination", arrLat+","+arrlLng);
        addParam("departure_time", departureTime+"");
        return callURL.toString();
    }

    public String getCallWithNoTransit(String tempCall, TravelMeanEnum type) {
        return tempCall + "&mode=" + type.getParam();
    }

    public String getCallByTransit(String tempCall, List<TravelMeanEnum> transitMeans) {
        StringBuilder callWithTravel = new StringBuilder(tempCall);
        if(transitMeans.size()>0) {
            callWithTravel.append("&mode=transit");
            callWithTravel.append("&transit_mode=" + transitMeans.get(0).getParam());
            for (int i = 1; i < transitMeans.size(); i++)
                callWithTravel.append("|" + transitMeans.get(i).getParam());
        }
        return callWithTravel.toString();
    }

    private void addParam (String param, String address) {
        if (callURL.charAt(getCallURL().length() - 1) != '?')
            callURL.append("&");
        callURL.append(param).append("=").append(address);
    }

    private void baseCall() {
        addParam("key", "AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y");
        addParam("alternatives", "true");
        addParam("units", "metric");
        addParam("avoid","ferries");
    }

}
