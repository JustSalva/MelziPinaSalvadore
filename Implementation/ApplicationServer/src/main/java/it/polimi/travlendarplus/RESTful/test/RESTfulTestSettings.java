package it.polimi.travlendarplus.RESTful.test;

import it.polimi.travlendarplus.beans.calendarManager.PathManager;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities.GMapsGeocoder;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsUnavailableException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class RESTfulTestSettings {
    @Inject
    PathManager pathManager;
    @Inject
    ScheduleManager scheduleManager;
    private Location lecco, mandello, como, maggianico, abbadia;
    private Event e1, e2, e3, e4;
    private BreakEvent be1, be2, be3;
    private Travel t1, t2, t3, t4;
    private TypeOfEvent toe1, toe2;
    private User user = new User();
    private PathCombination combination = new PathCombination( null, null );
    private ArrayList < TravelMeanEnum > privateMeans;
    private ArrayList < TravelMeanEnum > publicMeans;
    private TravelMeanEnum[] privM = { TravelMeanEnum.CAR };
    private TravelMeanEnum[] pubM = { TravelMeanEnum.TRAIN, TravelMeanEnum.BUS };

    public String addEventBaseCaseTest ( boolean first, boolean second, boolean third, boolean setTravel )
            throws GMapsUnavailableException {
        baseCaseConfiguration( first, second, third, setTravel );
        try {
            combination = pathManager.calculatePath(
                    ( !second ) ? e2 : ( !first ) ? e1 : e3, privateMeans, publicMeans );
        } catch ( GMapsGeneralException e ) {
            e.printStackTrace();
        }
        return combination.toString();
    }

    public String addEventWithBreak ( boolean first, boolean second, boolean third, boolean setTravel, long minInt )
            throws GMapsUnavailableException {
        baseCaseConfiguration( first, second, third, setTravel );
        //2018/01/20 h:12:00 - 13:00
        toe1 = setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
        e4 = setEvent( 1516449600, 1516453200, true, null, abbadia, toe1 );
        //e4 is the event that I try to add
        //2018/01/20 h:9:00 - 14:00
        be1 = setBreakEvent( 1516438800, 1516456800, minInt, true );
        user.addBreak( be1 );
        try {
            combination = pathManager.calculatePath( e4, privateMeans, publicMeans );
        } catch ( GMapsGeneralException e ) {
            e.printStackTrace();
        }
        return ( combination != null ) ? combination.toString() : "NO_FEASIBLE_PATHS";
    }

    public String addBreakEvent ( long stTime, long endTime, long minInt ) throws GMapsUnavailableException {
        baseCaseConfiguration( true, true, true, true );
        //2018/01/20 h:10:00 - 11:00
        be1 = setBreakEvent( 1516442400, 1516446000, 30 * 60, true );
        user.addBreak( be1 );
        //be2 is the break that we try to add
        be2 = setBreakEvent( stTime, endTime, minInt, false );
        return ( scheduleManager.isBreakOverlapFreeIntoSchedule( be2, false ) ) ? "OK" : "NO";
    }

    public String swapEvents ( long stTime, long endTime, boolean breakEvent ) throws GMapsUnavailableException {
        baseCaseConfiguration( true, true, true, true );
        toe1 = setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
        e4 = setEvent( stTime, endTime, true, lecco, maggianico, toe1 );
        String msg = "";
        //2018/01/20 h:10:00 - 11:00
        if ( breakEvent ) {
            be1 = setBreakEvent( 1516442400, 1516446000, 30 * 60, true );
            user.addBreak( be1 );
        }
        List< GenericEvent > swapResponse = null;
        try {
            swapResponse = pathManager.swapEvents( e4, privateMeans, publicMeans );
        } catch ( GMapsGeneralException e ) {
            e.printStackTrace();
        }
        for ( GenericEvent event : swapResponse )
            msg += event.toString() + "\n";
        return msg;
    }

    public void baseCaseConfiguration ( boolean first, boolean second, boolean third, boolean setTravels )
            throws GMapsUnavailableException {
        setBaseLocations();
        toe1 = setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
        //2018/01/20 h:8:00 - 10:00
        e1 = setEvent( 1516435200, 1516442400, false, mandello, mandello, toe1 );
        //2018/01/20 h:14:00 - 15:00
        e2 = setEvent( 1516456800, 1516460400, true, mandello, lecco, toe1 );
        //2018/01/20 h:18:00 - 20:00
        e3 = setEvent( 1516471200, 1516478400, true, mandello, como, toe1 );
        setScheduleld( first, second, third );
        if ( setTravels )
            baseTravelsConfiguration();
        ArrayList < Event > events = new ArrayList < Event >();
        events.add( e1 );
        events.add( e2 );
        events.add( e3 );
        user.setEvents( events );
        pathManager.setCurrentUser( user );
        setMeans( getMeansAL( privM ), getMeansAL( pubM ) );
    }

    public BreakEvent setBreakEvent ( long stTime, long endTime, long minTime, boolean sch ) {
        BreakEvent br = new BreakEvent();
        br.setName( "T+break" );
        br.setStartingTime( Instant.ofEpochSecond( stTime ) );
        br.setEndingTime( Instant.ofEpochSecond( endTime ) );
        br.setMinimumTime( minTime );
        br.setScheduled( sch );
        setUser();
        br.setUser( user );
        //user.save();
        //br.save();
        return br;
    }

    public void baseTravelsConfiguration () {
        //2018/01/20 h:4:00 - 5:00
        t1 = setTravel( 1516420800, 1516424400 );
        //t1.setRelatedEvent(e1);
        e1.setFeasiblePath( t1 );
        //2018/01/20 h:12:00 - 13:00
        t2 = setTravel( 1516449600, 1516453200 );
        //t2.setRelatedEvent(e2);
        e2.setFeasiblePath( t2 );
        //2018/01/20 h:16:00 - 18:00
        t3 = setTravel( 1516464000, 1516471200 );
        //t3.setRelatedEvent(e3);
        e3.setFeasiblePath( t3 );
    }

    public Travel setTravel ( long stTime, long endTime ) {
        Travel t = new Travel();
        ArrayList < TravelComponent > compList = new ArrayList < TravelComponent >();
        TravelComponent comp = new TravelComponent();
        comp.setStartingTime( Instant.ofEpochSecond( stTime ) );
        comp.setEndingTime( Instant.ofEpochSecond( endTime ) );
        compList.add( comp );
        t.setMiniTravels( compList );
        return t;
    }

    public Event setEvent ( long stTime, long endTime, boolean prevLoc, Location dep, Location arr, TypeOfEvent toe ) {
        Event e = new Event();
        e.setName( "T+" );
        e.setStartingTime( Instant.ofEpochSecond( stTime ) );
        e.setEndingTime( Instant.ofEpochSecond( endTime ) );
        e.setPrevLocChoice( prevLoc );
        e.setDeparture( dep );
        e.setEventLocation( arr );
        e.setType( toe );
        setUser();
        e.setUser( user );
        //user.save();
        //e.save();
        return e;
    }

    public void setUser () {
        user.setEmail( "test" );
    }

    public void setScheduleld ( boolean first, boolean second, boolean third ) {
        e1.setScheduled( first );
        e2.setScheduled( second );
        e3.setScheduled( third );
    }

    public Location setLocation ( double lat, double lng ) throws GMapsUnavailableException {
        return GMapsGeocoder.getLocationObject( lat, lng );
    }

    public void setBaseLocations () throws GMapsUnavailableException {
        lecco = setLocation( 45.8565698, 9.397670399999999 );
        mandello = setLocation( 45.91386989999999, 9.317738499999999 );
        como = setLocation( 45.8080597, 9.085176499999999 );
        maggianico = setLocation( 45.8259029, 9.419594 );
        abbadia = setLocation( 45.8948976, 9.336341900000001 );
        /*lecco = setLocation(51.500970, -0.124862);
        mandello = setLocation(51.502288, 0.045226);
        como = setLocation(51.502079, -0.174816);
        maggianico = setLocation(51.507857, -0.087833);
        abbadia = setLocation(51.503040, -0.137823);*/
        /*lecco.save();
        mandello.save();
        como.save();
        maggianico.save();
        abbadia.save();*/
    }

    public TypeOfEvent setTypeOfEvent ( String name, ParamFirstPath param ) {
        TypeOfEvent toe = new TypeOfEvent();
        toe.setName( name );
        toe.setParamFirstPath( param );
        //toe.save();
        return toe;
    }

    public ArrayList < TravelMeanEnum > getMeansAL ( TravelMeanEnum[] list ) {
        ArrayList < TravelMeanEnum > tme = new ArrayList < TravelMeanEnum >();
        for ( TravelMeanEnum mean : list )
            tme.add( mean );
        return tme;
    }

    public void setMeans ( ArrayList < TravelMeanEnum > privM, ArrayList < TravelMeanEnum > pubM ) {
        privateMeans = new ArrayList < TravelMeanEnum >( privM );
        publicMeans = new ArrayList < TravelMeanEnum >( pubM );
    }
}
