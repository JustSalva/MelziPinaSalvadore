package it.polimi.travlendarplus.RESTful.test;

import it.polimi.travlendarplus.beans.calendar_manager.PathManager;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsGeocoder;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.PathCombination;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.time.Instant;
import java.util.ArrayList;

@Path("test")
public class RESTfulTest {

    Event event1 = new Event();
    Event event2 = new Event();
    Event event3 = new Event();
    PathCombination combination;
    User user = new User();

    @Inject
    PathManager pathManager;

    @Path("calcPath")
    @GET
    @Produces("text/plain")
    public String baseTest() {
        RESTfulTestSettings.configureOne(this);
        ArrayList<TravelMeanEnum> privateMeans = new ArrayList<TravelMeanEnum>();
        ArrayList<TravelMeanEnum> publicMeans = new ArrayList<TravelMeanEnum>();
        privateMeans.add(TravelMeanEnum.CAR);
        publicMeans.add(TravelMeanEnum.BUS);
        publicMeans.add(TravelMeanEnum.TRAIN);
        combination = pathManager.calculatePath(event2, privateMeans, publicMeans, false);
        return combination.toString();
    }

    @Path("firstPath")
    @GET
    @Produces("text/plain")
    public String firstPathTest() {
        RESTfulTestSettings.configureTwo(this);
        ArrayList<TravelMeanEnum> privateMeans = new ArrayList<TravelMeanEnum>();
        ArrayList<TravelMeanEnum> publicMeans = new ArrayList<TravelMeanEnum>();
        privateMeans.add(TravelMeanEnum.CAR);
        privateMeans.add(TravelMeanEnum.BIKE);
        publicMeans.add(TravelMeanEnum.BUS);
        publicMeans.add(TravelMeanEnum.TRAIN);
        combination = pathManager.calculatePath(event1, privateMeans, publicMeans, false);
        return combination.toString();
    }

    @Path("lastPath")
    @GET
    @Produces("text/plain")
    public String lastPathTest() {
        RESTfulTestSettings.configureThree(this);
        ArrayList<TravelMeanEnum> privateMeans = new ArrayList<TravelMeanEnum>();
        ArrayList<TravelMeanEnum> publicMeans = new ArrayList<TravelMeanEnum>();
        privateMeans.add(TravelMeanEnum.CAR);
        privateMeans.add(TravelMeanEnum.BIKE);
        publicMeans.add(TravelMeanEnum.BUS);
        publicMeans.add(TravelMeanEnum.TRAIN);
        combination = pathManager.calculatePath(event3, privateMeans, publicMeans, false);
        return combination.toString();
    }

    @Path("swap")
    @GET
    @Produces("text/plain")
    public String swap() {
        RESTfulTestSettings.configureFour(this);
        ArrayList<TravelMeanEnum> privateMeans = new ArrayList<TravelMeanEnum>();
        ArrayList<TravelMeanEnum> publicMeans = new ArrayList<TravelMeanEnum>();
        privateMeans.add(TravelMeanEnum.CAR);
        privateMeans.add(TravelMeanEnum.BIKE);
        publicMeans.add(TravelMeanEnum.BUS);
        publicMeans.add(TravelMeanEnum.TRAIN);
        Event event4 = new Event();
        Location maggianico = GMapsGeocoder.getLocationObject(45.8259029, 9.419594);
        Location lecco = GMapsGeocoder.getLocationObject(45.8565698, 9.397670399999999);
        event4.setDeparture(lecco);
        event4.setEventLocation(maggianico);
        //2018/01/20 h:14:30 - 17:00
        event4.setStartingTime(Instant.ofEpochSecond(1516458600));
        event4.setEndingTime(Instant.ofEpochSecond(1516467600));
        event4.setType(new TypeOfEvent("", null));
        event4.setPrevLocChoice(true);
        String msg = "";
        for(Event event: pathManager.swapEvents(event4, privateMeans, publicMeans).getEvents())
            msg+="***"+event.toString()+"\n";
        return msg;
    }
}
