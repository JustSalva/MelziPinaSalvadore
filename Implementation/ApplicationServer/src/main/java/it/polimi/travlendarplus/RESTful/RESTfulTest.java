package it.polimi.travlendarplus.RESTful;

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

    @Inject
    PathManager pathManager;

    @Path("calcPath")
    @GET
    @Produces("text/plain")
    public String prova() {
        Event event1 = new Event();
        Event event2 = new Event();
        Event event3 = new Event();
        PathCombination combination;
        User user = new User();

        Location lecco = setLocation(45.8565698, 9.397670399999999);
        Location mandello = setLocation(45.91386989999999, 9.317738499999999);
        Location como = setLocation(45.8080597, 9.085176499999999);
        //2018/01/20 h:8:00 - 10:00
        event1 = setEvent(event1, 1, 1516435200, 1516442400, true, mandello, mandello);
        //2018/01/20 h:18:00 - 20:00
        event3 = setEvent(event3, 3, 1516471200, 1516478400, true, mandello, como);
        //2018/01/20 h:14:00 - 15:00
        event2 = setEvent(event2, 2, 1516456800, 1516460400, false, mandello, lecco);
        //manager.setSchedule(test.event1.getDayAtMidnight());
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(event1);
        events.add(event2);
        events.add(event3);
        user.setEvents(events);
        pathManager.setCurrentUser(user);

        ArrayList<TravelMeanEnum> privateMeans = new ArrayList<TravelMeanEnum>();
        ArrayList<TravelMeanEnum> publicMeans = new ArrayList<TravelMeanEnum>();
        privateMeans.add(TravelMeanEnum.CAR);
        publicMeans.add(TravelMeanEnum.BUS);
        publicMeans.add(TravelMeanEnum.TRAIN);
        combination = pathManager.calculatePath(event2, privateMeans, publicMeans, false);

        return combination.toString();
    }


    public static Event setEvent (Event e, long id, long stTime, long endTime,  boolean sch, Location dep, Location arr) {
        e.setId(id);
        e.setStartingTime(Instant.ofEpochSecond(stTime));
        e.setEndingTime(Instant.ofEpochSecond(endTime));
        e.setScheduled(sch);
        e.setDeparture(dep);
        e.setEventLocation(arr);
        return e;
    }

    public static Location setLocation(double lat, double lng) {
        return GMapsGeocoder.getLocationObject(lat, lng);
    }
}
