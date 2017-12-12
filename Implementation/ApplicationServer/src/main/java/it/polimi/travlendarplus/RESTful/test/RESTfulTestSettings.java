package it.polimi.travlendarplus.RESTful.test;

import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsGeocoder;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.calendar.Event;

import java.time.Instant;
import java.util.ArrayList;

public class RESTfulTestSettings {
    static Location lecco, mandello, como;

    public static void setBaseLocations() {
        lecco = setLocation(45.8565698, 9.397670399999999);
        mandello = setLocation(45.91386989999999, 9.317738499999999);
        como = setLocation(45.8080597, 9.085176499999999);
    }

    public static void configureOne(RESTfulTest test){
        setBaseLocations();
        //2018/01/20 h:8:00 - 10:00
        test.event1 = setEvent(test.event1, 1, 1516435200, 1516442400, true, mandello, mandello);
        //2018/01/20 h:18:00 - 20:00
        test.event3 = setEvent(test.event3, 3, 1516471200, 1516478400, true, mandello, como);
        //2018/01/20 h:14:00 - 15:00
        test.event2 = setEvent(test.event2, 2, 1516456800, 1516460400, false, mandello, lecco);
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(test.event1);
        events.add(test.event2);
        events.add(test.event3);
        test.user.setEvents(events);
        test.pathManager.setCurrentUser(test.user);
    }

    public static void configureTwo(RESTfulTest test) {
        setBaseLocations();
        //2018/01/20 h:8:00 - 10:00
        test.event1 = setEvent(test.event1, 1, 1516435200, 1516442400, false, mandello, lecco);
        //2018/01/20 h:18:00 - 20:00
        test.event3 = setEvent(test.event3, 3, 1516471200, 1516478400, true, lecco, como);
        //2018/01/20 h:14:00 - 15:00
        test.event2 = setEvent(test.event2, 2, 1516456800, 1516460400, true, lecco, lecco);
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(test.event1);
        events.add(test.event2);
        events.add(test.event3);
        test.user.setEvents(events);
        test.pathManager.setCurrentUser(test.user);
    }

    public static void configureThree(RESTfulTest test) {
        setBaseLocations();
        //2018/01/20 h:8:00 - 10:00
        test.event1 = setEvent(test.event1, 1, 1516435200, 1516442400, true, mandello, mandello);
        //2018/01/20 h:18:00 - 20:00
        test.event3 = setEvent(test.event3, 3, 1516471200, 1516478400, false, lecco, como);
        //2018/01/20 h:14:00 - 15:00
        test.event2 = setEvent(test.event2, 2, 1516456800, 1516460400, true, mandello, lecco);
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(test.event1);
        events.add(test.event2);
        events.add(test.event3);
        test.user.setEvents(events);
        test.pathManager.setCurrentUser(test.user);
    }

    public static Event setEvent (Event e, long id, long stTime, long endTime, boolean sch, Location dep, Location arr) {
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
