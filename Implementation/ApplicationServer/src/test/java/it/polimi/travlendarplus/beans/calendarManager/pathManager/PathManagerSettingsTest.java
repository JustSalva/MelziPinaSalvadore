package it.polimi.travlendarplus.beans.calendarManager.pathManager;

import it.polimi.travlendarplus.RESTful.test.RESTfulTestSettings;
import it.polimi.travlendarplus.beans.calendarManager.PathManager;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.beans.calendarManager.support.ScheduleHolder;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import java.time.Instant;
import java.util.ArrayList;

public class PathManagerSettingsTest {
    static Location lecco, mandello, como, maggianico, abbadia;
    static Event e1, e2, e3, e4;
    static Event e1sw, e2sw, e3sw, e4sw, e5sw;
    static BreakEvent br1sw, br2sw;
    static TypeOfEvent toe1;
    static User user = new User();
    static ArrayList < TravelMeanEnum > publicMeans, privateMeans;
    static RESTfulTestSettings testSettings = new RESTfulTestSettings();

    public static void baseSet() {
        setLocations();
        toe1 = testSettings.setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
    }

    public static PathCombination getPathCombinationTest ( PathManager pm, Event ev ) {
        pm.setCurrentUser( user );
        privateMeans = new ArrayList < TravelMeanEnum >();
        publicMeans = new ArrayList < TravelMeanEnum >();
        publicMeans.add( TravelMeanEnum.BUS );
        privateMeans.add( TravelMeanEnum.CAR );
        return pm.calculatePath( ev, privateMeans, publicMeans );

    }

    public static void baseConfiguration ( boolean first, boolean second, boolean third ) {
        setLocations();
        toe1 = testSettings.setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
        //2018/01/20 h:8:00 - 10:00
        e1 = testSettings.setEvent( 1516435200, 1516442400, false, mandello, lecco, toe1 );
        //2018/01/20 h:14:00 - 15:00
        e2 = testSettings.setEvent( 1516456800, 1516460400, false, mandello, lecco, toe1 );
        //2018/01/20 h:18:00 - 20:00
        e3 = testSettings.setEvent( 1516471200, 1516478400, true, mandello, como, toe1 );
        e1.setScheduled( first );
        e2.setScheduled( second );
        e3.setScheduled( third );
        ArrayList < Event > events = new ArrayList < Event >();
        ArrayList < BreakEvent > breaks = new ArrayList < BreakEvent >();
        events.add( e1 );
        events.add( e2 );
        events.add( e3 );
        // set e4
        setEventToAdd();
        user.setEvents( events );
    }

    public static void setEventToAdd () {
        //2018/01/20 h:12:00 - 13:00
        e4 = testSettings.setEvent( 1516449600, 1516453200, true, null, abbadia, toe1 );
        //e4 is the event that I try to add
    }

    public static ScheduleHolder createScheduleHolder ( boolean one, boolean two, boolean three, boolean four, boolean five, 
                                                        boolean brOne, boolean brTwo) {
        ArrayList<Event> events = new ArrayList<Event>();
        ArrayList<BreakEvent> breaks = new ArrayList <BreakEvent>(  );
        //2018/01/20 h:8:00 - 10:00
        e1sw = setEvent( 1, 1516435200, 1516442400, false, true, mandello, mandello, toe1 );
        //2018/01/20 h:7:00 - 8:00
        e1sw.setFeasiblePath( setTravel( 1516431600, 1516435200 ) );
        if(one) {
            e1sw.setScheduled( true );
            events.add(e1sw);
        }

        //2018/01/20 h:12:00 - 13:00
        e2sw = setEvent(2, 1516449600, 1516453200, true, false, mandello, abbadia, toe1 );
        //2018/01/20 h:10:00 - 11:00
        e2sw.setFeasiblePath( setTravel( 1516442400, 1516446000 ) );
        if(two) {
            e2sw.setScheduled( true );
            events.add(e2sw);
        }

        //2018/01/20 h:15:00 - 16:00
        e3sw = setEvent(3, 1516460400, 1516464000, true, false, abbadia, maggianico, toe1 );
        //2018/01/20 h:13:00 - 13:30
        e3sw.setFeasiblePath( setTravel( 1516453200, 1516455000 ) );
        if(three) {
            e3sw.setScheduled( true );
            events.add(e3sw);
        }

        //2018/01/20 h:18:00 - 19:00
        e4sw = setEvent(4, 1516471200, 1516474800, true, false, maggianico, lecco, toe1 );
        //2018/01/20 h:16:00 - 17:00
        e4sw.setFeasiblePath( setTravel( 1516464000, 1516467600 ) );
        if(four) {
            e4sw.setScheduled( true );
            events.add(e4sw);
        }

        //2018/01/20 h:21:00 - 22:00
        e5sw = setEvent(5, 1516482000, 1516485600, true, false, lecco, como, toe1 );
        //2018/01/20 h:19:00 - 19:30
        e5sw.setFeasiblePath( setTravel( 1516474800, 1516476600 ) );
        if(five) {
            e5sw.setScheduled( true );
            events.add(e5sw);
        }

        //2018/01/20 h:16:30 - 18:30 minimum: 30 min
        br1sw = setBreakEvent( 10, 1516465800, 1516473000, 1800 );
        if(brOne) {
            br1sw.setScheduled( true );
            breaks.add(br1sw);
        }

        //2018/01/20 h:19:30 - 21:30 minimum: 60 min
        br2sw = setBreakEvent( 10, 1516476600, 1516483800, 1800 );
        if(brTwo) {
            br1sw.setScheduled( true );
            breaks.add( br2sw );
        }

        return new ScheduleHolder(events, breaks);
    }

    public static Event setEvent(long id, long stTime, long endTime, boolean prevLoc, boolean travAtL, Location dep, Location arr, TypeOfEvent toe) {
        Event e = new Event();
        e.setName( "Event T+" );
        e.setId( id );
        e.setStartingTime( Instant.ofEpochSecond( stTime ) );
        e.setEndingTime( Instant.ofEpochSecond( endTime ) );
        e.setPrevLocChoice( prevLoc );
        e.setTravelAtLastChoice( travAtL );
        e.setDeparture( dep );
        e.setEventLocation( arr );
        e.setType( toe );
        return e;
    }

    public static Travel setTravel( long stTime, long endTime ) {
        Travel t = new Travel();
        ArrayList<TravelComponent > tc = new ArrayList<TravelComponent>();
        TravelComponent comp = new TravelComponent();
        comp.setStartingTime( Instant.ofEpochSecond( stTime ) );
        comp.setEndingTime( Instant.ofEpochSecond( endTime ) );
        tc.add(comp);
        t.setMiniTravels( tc );
        return t;
    }

    public static BreakEvent setBreakEvent(long id, long stTime, long endTime, int minTime) {
        BreakEvent brE = new BreakEvent();
        brE.setName( "Break T+" );
        brE.setId( id );
        brE.setStartingTime( Instant.ofEpochSecond( stTime ));
        brE.setEndingTime( Instant.ofEpochSecond( endTime ) );
        brE.setMinimumTime( minTime );
        return brE;
    }

    public static void setLocations () {
        lecco = new Location( 45.8565698, 9.397670399999999, "Lecco,Italy" );
        mandello = new Location( 45.91386989999999, 9.317738499999999, "Mandello,Italy" );
        como = new Location( 45.8080597, 9.085176499999999, "Como,Italy" );
        maggianico = new Location( 45.8259029, 9.419594, "Maggianico,Italy" );
        abbadia = new Location( 45.8948976, 9.336341900000001, "Abbadia,Italy" );
    }
}
