package it.polimi.travlendarplus.beans.calendarManager.pathManager;

import it.polimi.travlendarplus.RESTful.test.RESTfulTestSettings;
import it.polimi.travlendarplus.beans.calendarManager.PathManager;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;

import java.util.ArrayList;

public class PathManagerSettingsTest {
    static Location lecco, mandello, como, maggianico, abbadia;
    static Event e1, e2, e3, e4;
    static Travel t1, t2, t3;
    static TypeOfEvent toe1;
    static User user = new User();
    static ArrayList < TravelMeanEnum > publicMeans, privateMeans;
    static RESTfulTestSettings testSettings = new RESTfulTestSettings();

    public static PathCombination getPathCombinationTest ( PathManager pm, Event ev ) {
        pm.setCurrentUser( user );
        privateMeans = new ArrayList < TravelMeanEnum >();
        publicMeans = new ArrayList < TravelMeanEnum >();
        publicMeans.add( TravelMeanEnum.BUS );
        privateMeans.add( TravelMeanEnum.CAR );
        return pm.calculatePath( ev, privateMeans, publicMeans );

    }

    public static void baseConfiguration ( boolean first, boolean second, boolean third ) {
        lecco = new Location( 45.8565698, 9.397670399999999, "Lecco,Italy" );
        mandello = new Location( 45.91386989999999, 9.317738499999999, "Mandello,Italy" );
        como = new Location( 45.8080597, 9.085176499999999, "Como,Italy" );
        maggianico = new Location( 45.8259029, 9.419594, "Maggianico,Italy" );
        abbadia = new Location( 45.8948976, 9.336341900000001, "Abbadia,Italy" );
        toe1 = testSettings.setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
        //2018/01/20 h:8:00 - 10:00
        e1 = testSettings.setEvent( 1516435200, 1516442400, false, mandello, mandello, toe1 );
        //2018/01/20 h:14:00 - 15:00
        e2 = testSettings.setEvent( 1516456800, 1516460400, true, mandello, lecco, toe1 );
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


}
