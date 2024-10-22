package it.polimi.travlendarplus.beans.calendarManager.pathManager;

import it.polimi.travlendarplus.TestUtilities;
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
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;

import java.util.ArrayList;

public class PathManagerSettings {
    static Location lecco, mandello, como, maggianico, abbadia;
    static Event e1, e2, e3, e4;
    static Event e1sw, e2sw, e3sw, e4sw, e5sw;
    static BreakEvent br1sw, br2sw;
    static TypeOfEvent toe1;
    static User user = new User();
    static ArrayList < TravelMeanEnum > publicMeans, privateMeans;

    public static void baseSet () {
        setLocations();
        toe1 = TestUtilities.setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
    }

    public static PathCombination getPathCombinationTest ( PathManager pm, Event ev ) throws GMapsGeneralException {
        pm.setCurrentUser( user );
        privateMeans = new ArrayList < TravelMeanEnum >();
        publicMeans = new ArrayList < TravelMeanEnum >();
        publicMeans.add( TravelMeanEnum.BUS );
        privateMeans.add( TravelMeanEnum.CAR );
        return pm.calculatePath( ev, privateMeans, publicMeans );

    }

    public static void baseConfiguration ( boolean first, boolean second, boolean third ) {
        setLocations();
        toe1 = TestUtilities.setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
        //2018/01/20 h:8:00 - 10:00
        e1 = TestUtilities.setEvent( 1, 1516435200, 1516442400, false, false,
                mandello, lecco, toe1, user );
        //2018/01/20 h:14:00 - 15:00
        e2 = TestUtilities.setEvent( 2, 1516456800, 1516460400, false, false,
                mandello, lecco, toe1, user );
        //2018/01/20 h:18:00 - 20:00
        e3 = TestUtilities.setEvent( 3, 1516471200, 1516478400, true, false,
                mandello, como, toe1, user );
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
        e4 = TestUtilities.setEvent( 4, 1516449600, 1516453200, true, false,
                null, abbadia, toe1, user );
        //e4 is the event that I try to add
    }

    public static ScheduleHolder createScheduleHolder ( boolean one, boolean two, boolean three, boolean four, boolean five,
                                                        boolean brOne, boolean brTwo ) {
        ArrayList < Event > events = new ArrayList < Event >();
        ArrayList < BreakEvent > breaks = new ArrayList < BreakEvent >();
        //2018/01/20 h:8:00 - 10:00
        e1sw = TestUtilities.setEvent( 1, 1516435200, 1516442400, false, true,
                mandello, mandello, toe1, user );
        //2018/01/20 h:7:00 - 8:00
        e1sw.setFeasiblePath( TestUtilities.setTravel( 1516431600, 1516435200 ) );
        if ( one ) {
            e1sw.setScheduled( true );
            events.add( e1sw );
        }

        //2018/01/20 h:12:00 - 13:00
        e2sw = TestUtilities.setEvent( 2, 1516449600, 1516453200, true, false,
                mandello, abbadia, toe1, user );
        //2018/01/20 h:10:00 - 11:00
        e2sw.setFeasiblePath( TestUtilities.setTravel( 1516442400, 1516446000 ) );
        if ( two ) {
            e2sw.setScheduled( true );
            events.add( e2sw );
        }

        //2018/01/20 h:15:00 - 16:00
        e3sw = TestUtilities.setEvent( 3, 1516460400, 1516464000, true, false,
                abbadia, maggianico, toe1, user );
        //2018/01/20 h:13:00 - 13:30
        e3sw.setFeasiblePath( TestUtilities.setTravel( 1516453200, 1516455000 ) );
        if ( three ) {
            e3sw.setScheduled( true );
            events.add( e3sw );
        }

        //2018/01/20 h:18:00 - 19:00
        e4sw = TestUtilities.setEvent( 4, 1516471200, 1516474800, true, false,
                maggianico, lecco, toe1, user );
        //2018/01/20 h:16:00 - 17:00
        e4sw.setFeasiblePath( TestUtilities.setTravel( 1516464000, 1516467600 ) );
        if ( four ) {
            e4sw.setScheduled( true );
            events.add( e4sw );
        }

        //2018/01/20 h:21:00 - 22:00
        e5sw = TestUtilities.setEvent( 5, 1516482000, 1516485600, true, false,
                lecco, como, toe1, user );
        //2018/01/20 h:19:00 - 19:30
        e5sw.setFeasiblePath( TestUtilities.setTravel( 1516474800, 1516476600 ) );
        if ( five ) {
            e5sw.setScheduled( true );
            events.add( e5sw );
        }

        //2018/01/20 h:16:30 - 18:30 minimum: 30 min
        br1sw = TestUtilities.setBreakEvent( 10, 1516465800, 1516473000, 1800, user );
        if ( brOne ) {
            br1sw.setScheduled( true );
            breaks.add( br1sw );
        }

        //2018/01/20 h:19:30 - 21:30 minimum: 60 min
        br2sw = TestUtilities.setBreakEvent( 11, 1516476600, 1516483800, 3600, user );
        if ( brTwo ) {
            br1sw.setScheduled( true );
            breaks.add( br2sw );
        }

        return new ScheduleHolder( events, breaks );
    }

    public static void setLocations () {
        lecco = new Location( 45.8565698, 9.397670399999999, "Lecco,Italy" );
        mandello = new Location( 45.91386989999999, 9.317738499999999, "Mandello,Italy" );
        como = new Location( 45.8080597, 9.085176499999999, "Como,Italy" );
        maggianico = new Location( 45.8259029, 9.419594, "Maggianico,Italy" );
        abbadia = new Location( 45.8948976, 9.336341900000001, "Abbadia,Italy" );
    }
}
