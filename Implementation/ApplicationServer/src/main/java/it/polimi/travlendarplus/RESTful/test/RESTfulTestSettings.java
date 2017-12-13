package it.polimi.travlendarplus.RESTful.test;

import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsGeocoder;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import java.time.Instant;
import java.util.ArrayList;

public class RESTfulTestSettings {
    static Location lecco, mandello, como, maggianico;

    public static void setBaseLocations() {
        lecco = setLocation(45.8565698, 9.397670399999999);
        mandello = setLocation(45.91386989999999, 9.317738499999999);
        como = setLocation(45.8080597, 9.085176499999999);
        maggianico = setLocation(45.8259029, 9.419594);
    }

    public static void configureOne(RESTfulTest test){
        setBaseLocations();
        //2018/01/20 h:8:00 - 10:00
        test.event1 = setEvent(test.event1, 1, 1516435200, 1516442400, true, mandello, mandello);
        //2018/01/20 h:18:00 - 20:00
        test.event3 = setEvent(test.event3, 3, 1516471200, 1516478400, true, lecco, como);
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

    public static void configureFour(RESTfulTest test) {
        setBaseLocations();
        //2018/01/20 h:5:00 - 6:00
        test.event1 = setEvent(test.event1, 1, 1516424400, 1516428000, true, mandello, mandello);
        //2018/01/20 h:14:00 - 15:00
        test.event2 = setEvent(test.event2, 2, 1516456800, 1516460400, true, mandello, lecco);
        //2018/01/20 h:18:00 - 20:00
        test.event3 = setEvent(test.event3, 3, 1516471200, 1516478400, true, maggianico, como);
        setTravels(test);
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(test.event1);
        events.add(test.event2);
        events.add(test.event3);
        test.user.setEvents(events);
        test.pathManager.setCurrentUser(test.user);
    }

    private static void setTravels(RESTfulTest test) {
        Travel t1 = new Travel();
        Travel t2 = new Travel();
        Travel t3 = new Travel();
        TravelComponent comp = new TravelComponent();
        ArrayList<TravelComponent> listComp = new ArrayList<TravelComponent>();
        //2018/01/20 h:4:00 - 5:00
        comp.setStartingTime(Instant.ofEpochSecond(1516420800));
        comp.setEndingTime(Instant.ofEpochSecond(1516424400));
        listComp.add(comp);
        t1.setMiniTravels(listComp);
        test.event1.setFeasiblePath(t1);
        //2018/01/20 h:12:00 - 13:00
        comp.setStartingTime(Instant.ofEpochSecond(1516449600));
        comp.setEndingTime(Instant.ofEpochSecond(1516453200));
        listComp = new ArrayList<TravelComponent>();
        listComp.add(comp);
        t2.setMiniTravels(listComp);
        test.event2.setFeasiblePath(t2);
        //2018/01/20 h:16:00 - 18:00
        comp.setStartingTime(Instant.ofEpochSecond(1516464000));
        comp.setEndingTime(Instant.ofEpochSecond(1516471200));
        listComp = new ArrayList<TravelComponent>();
        listComp.add(comp);
        t3.setMiniTravels(listComp);
        test.event3.setFeasiblePath(t3);
    }

    public static Event setEvent (Event e, long id, long stTime, long endTime, boolean sch, Location dep, Location arr) {
        e.setId(id);
        e.setStartingTime(Instant.ofEpochSecond(stTime));
        e.setEndingTime(Instant.ofEpochSecond(endTime));
        e.setScheduled(sch);
        e.setDeparture(dep);
        e.setEventLocation(arr);
        e.setType(new TypeOfEvent("",null));
        return e;
    }

    public static Location setLocation(double lat, double lng) {
        return GMapsGeocoder.getLocationObject(lat, lng);
    }
}
