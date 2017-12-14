package it.polimi.travlendarplus.RESTful.test;

import it.polimi.travlendarplus.beans.calendar_manager.PathManager;
import it.polimi.travlendarplus.beans.calendar_manager.ScheduleManager;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsGeocoder;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.PathCombination;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.ScheduleHolder;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class RESTfulTestSettings {
    private Location lecco, mandello, como, maggianico, abbadia;
    private Event e1, e2, e3, e4;
    private BreakEvent be1, be2, be3;
    private Travel t1, t2, t3, t4;
    private TypeOfEvent toe1, toe2;
    private User user = new User();
    private PathCombination combination = new PathCombination(null, null);
    private ArrayList<TravelMeanEnum> privateMeans;
    private ArrayList<TravelMeanEnum> publicMeans;
    private TravelMeanEnum[] privM = {TravelMeanEnum.CAR, TravelMeanEnum.BIKE};
    private TravelMeanEnum[] pubM = {TravelMeanEnum.TRAIN, TravelMeanEnum.BUS};

    @Inject
    PathManager pathManager;
    @Inject
    ScheduleManager scheduleManager;

    public String addEventBaseCaseTest(boolean first, boolean second, boolean third, boolean setTravel) {
        baseCaseConfiguration(first, second, third, setTravel);
        combination = pathManager.calculatePath((!second) ? e2 : (!first) ? e1 : e3, privateMeans, publicMeans, false);
        return combination.toString();
    }

    public String addEventWithBreak(boolean first, boolean second, boolean third, boolean setTravel, long minInt) {
        baseCaseConfiguration(first, second, third, setTravel);
        //2018/01/20 h:12:00 - 13:00
        toe1 = setTypeOfEvent("");
        e4 = setEvent(4,1516449600,1516453200, true, null, abbadia, toe1);
        //e4 is the event that I try to add
        //2018/01/20 h:9:00 - 14:00
        be1 = setBreakEvent(1516438800, 1516456800, minInt, true);
        user.addBreak(be1);
        combination = pathManager.calculatePath(e4, privateMeans, publicMeans, false);
        return (combination != null) ? combination.toString() : "NO_FEASIBLE_PATHS";
    }

    public String addBreakEvent(long stTime, long endTime, long minInt) {
        baseCaseConfiguration(true, true, true, true);
        //2018/01/20 h:10:00 - 11:00
        be1 = setBreakEvent(1516442400, 1516446000, 30*60, true);
        user.addBreak(be1);
        //be2 is the break that we try to add
        be2 = setBreakEvent(stTime, endTime, minInt, false);
        return (scheduleManager.isBreakOverlapFreeIntoSchedule(be2, false)) ? "OK" : "NO";
    }

    public String swapEvents(long stTime, long endTime, boolean breakEvent) {
        baseCaseConfiguration(true, true, true, true);
        toe1 = setTypeOfEvent("");
        e4 = setEvent(4,stTime, endTime, true, lecco, maggianico, toe1);
        String msg = "";
        //2018/01/20 h:10:00 - 11:00
        if(breakEvent) {
            be1 = setBreakEvent(1516442400, 1516446000, 30 * 60, true);
            user.addBreak(be1);
        }
        ScheduleHolder sch = pathManager.swapEvents(e4, privateMeans, publicMeans);
        for(Event event: sch.getEvents())
            msg += "E - "+event.toString()+"\n";
        for(BreakEvent br: sch.getBreaks())
            msg += "B - "+br.toString()+"\n";
        return msg;
    }

    private void baseCaseConfiguration(boolean first, boolean second, boolean third, boolean setTravels) {
        setBaseLocations();
        toe1 = setTypeOfEvent("");
        //2018/01/20 h:8:00 - 10:00
        e1 = setEvent(1, 1516435200, 1516442400, false, mandello, mandello, toe1);
        //2018/01/20 h:14:00 - 15:00
        e2 = setEvent(2, 1516456800, 1516460400, true, mandello, lecco, toe1);
        //2018/01/20 h:18:00 - 20:00
        e3 = setEvent(3, 1516471200, 1516478400, true, mandello, como, toe1);
        setScheduleld(first, second, third);
        if(setTravels)
            baseTravelsConfiguration();
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(e1);
        events.add(e2);
        events.add(e3);
        user.setEvents(events);
        pathManager.setCurrentUser(user);
        setMeans(getMeansAL(privM), getMeansAL(pubM));
    }

    private BreakEvent setBreakEvent(long stTime, long endTime, long minTime, boolean sch) {
        BreakEvent br = new BreakEvent();
        br.setStartingTime(Instant.ofEpochSecond(stTime));
        br.setEndingTime(Instant.ofEpochSecond(endTime));
        br.setMinimumTime(minTime);
        br.setScheduled(sch);
        return br;
    }

    private void baseTravelsConfiguration() {
        //2018/01/20 h:4:00 - 5:00
        t1 = setTravel(1516420800, 1516424400);
        //t1.setRelatedEvent(e1);
        e1.setFeasiblePath(t1);
        //2018/01/20 h:12:00 - 13:00
        t2 = setTravel(1516449600, 1516453200);
        //t2.setRelatedEvent(e2);
        e2.setFeasiblePath(t2);
        //2018/01/20 h:16:00 - 18:00
        t3 = setTravel(1516464000, 1516471200);
        //t3.setRelatedEvent(e3);
        e3.setFeasiblePath(t3);
    }

    private Travel setTravel(long stTime, long endTime) {
        Travel t = new Travel();
        ArrayList<TravelComponent> compList = new ArrayList<TravelComponent>();
        TravelComponent comp = new TravelComponent();
        comp.setStartingTime(Instant.ofEpochSecond(stTime));
        comp.setEndingTime(Instant.ofEpochSecond(endTime));
        compList.add(comp);
        t.setMiniTravels(compList);
        return t;
    }

    private Event setEvent (long id, long stTime, long endTime, boolean prevLoc, Location dep, Location arr, TypeOfEvent toe) {
        Event e = new Event();
        e.setId(id);
        e.setStartingTime(Instant.ofEpochSecond(stTime));
        e.setEndingTime(Instant.ofEpochSecond(endTime));
        e.setPrevLocChoice(prevLoc);
        e.setDeparture(dep);
        e.setEventLocation(arr);
        e.setType(toe);
        return e;
    }

    private void setScheduleld(boolean first, boolean second, boolean third) {
        e1.setScheduled(first);
        e2.setScheduled(second);
        e3.setScheduled(third);
    }

    private Location setLocation(double lat, double lng) {
        return GMapsGeocoder.getLocationObject(lat, lng);
    }

    private void setBaseLocations() {
        lecco = setLocation(45.8565698, 9.397670399999999);
        mandello = setLocation(45.91386989999999, 9.317738499999999);
        como = setLocation(45.8080597, 9.085176499999999);
        maggianico = setLocation(45.8259029, 9.419594);
        abbadia = setLocation(45.8948976, 9.336341900000001);
    }

    private TypeOfEvent setTypeOfEvent(String name) {
        TypeOfEvent toe = new TypeOfEvent();
        toe.setName(name);
        return toe;
    }

    private ArrayList<TravelMeanEnum> getMeansAL(TravelMeanEnum[] list) {
        ArrayList<TravelMeanEnum> tme = new ArrayList<TravelMeanEnum>();
        for(TravelMeanEnum mean: list)
            tme.add(mean);
        return tme;
    }

    private void setMeans(ArrayList<TravelMeanEnum> privM, ArrayList<TravelMeanEnum> pubM) {
        privateMeans = new ArrayList<TravelMeanEnum>(privM);
        publicMeans = new ArrayList<TravelMeanEnum>(pubM);
    }
}
