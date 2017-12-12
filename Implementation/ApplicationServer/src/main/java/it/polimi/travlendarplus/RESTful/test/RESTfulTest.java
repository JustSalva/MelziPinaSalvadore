package it.polimi.travlendarplus.RESTful.test;

import it.polimi.travlendarplus.beans.calendar_manager.PathManager;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsGeocoder;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.PathCombination;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.Event;
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

}
