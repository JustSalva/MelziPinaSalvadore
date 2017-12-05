package it.polimi.travlendarplus.beans.calendar_manager.support;

import it.polimi.travlendarplus.beans.calendar_manager.PathManager;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

import java.time.Instant;
import java.util.ArrayList;

public class GMapsURL {
    private StringBuilder callURL;

    public GMapsURL() {
        callURL = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        baseCall();
    }

    public GMapsURL(String callURL) {
        this.callURL = new StringBuilder(callURL);
    }

    public String getCallURL() {
        return callURL.toString();
    }

    // getBaseCallPreviousPath() & getBaseCallFollowingPath() contain parameters for origin, destination and
    // departure_time. They return a String that can be enriched with other params using the functions defined
    // in this class.

    //the eventual path related to this event: origin is yet setted into the event object's field
    public String getBaseCallPreviousPath(Event event, Event previousEvent) {
        long secondi = (48*12*31*24*60*60);
        addParam("origin", "Lecco,Italy");
        addParam("destination", "Como,Italy");
        //addParam("departure_time", secondi+"");
        //addParam("departure_time", Instant.now().getEpochSecond()+"");
        addParam("departure_time", 1517262180+"" );
        //addParam("mode", "transit");
        //addParam("transit_mode", "train");
        //System.out.println(getCallURL());
        return callURL.toString();
        /*      addParam("origin", event.getDeparture().getAddress());
                addParam("destination", event.getEventLocation().getAddress());
                addParam("departure_time", previousEvent.getEndingTime().toString());
                return callURL.toString();
        */
    }

    //the eventual path will be linked to the following event
    public String getBaseCallFollowingPath(Event event, Event followingEvent) {
        addParam("origin", event.getEventLocation().getAddress());
        addParam("destination", followingEvent.getEventLocation().getAddress());
        addParam("departure_time", event.getEndingTime().toString());
        return callURL.toString();
    }

    public String getCallWithBicycle(String tempCall) {
        return tempCall + "&mode=" + TravelMeanEnum.BIKE.getParam();
    }

    public String getCallByWalk(String tempCall) {
        return tempCall + "&mode=" + TravelMeanEnum.BY_FOOT.getParam();
    }

    public String getCallByTransit(String tempCall, ArrayList<TravelMeanEnum> transitMeans) {
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



//https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y