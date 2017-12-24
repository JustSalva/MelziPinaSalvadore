package it.polimi.travlendarplus.RESTful.test;

import it.polimi.travlendarplus.TestUtilities;
import it.polimi.travlendarplus.beans.calendarManager.PathManager;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
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
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.BadRequestException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsUnavailableException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.LocationNotFoundException;

import javax.ejb.Stateless;
import javax.inject.Inject;
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
    private TravelMeanEnum[] privM = new TravelMeanEnum[ 0 ];//{ TravelMeanEnum.CAR };
    private TravelMeanEnum[] pubM = { TravelMeanEnum.TRAIN, TravelMeanEnum.BUS };

    public String addEventBaseCaseTest ( boolean first, boolean second, boolean third, boolean setTravel )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
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
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        baseCaseConfiguration( first, second, third, setTravel );
        //2018/01/20 h:12:00 - 13:00
        toe1 = TestUtilities.setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
        e4 = TestUtilities.setEvent( 4, 1516449600, 1516453200, true, false,
                null, abbadia, toe1, user );
        //e4 is the event that I try to add
        //2018/01/20 h:9:00 - 14:00
        be1 = TestUtilities.setBreakEvent( 101, 1516438800, 1516456800, minInt, user );
        be1.setScheduled( true );
        user.addBreak( be1 );
        try {
            combination = pathManager.calculatePath( e4, privateMeans, publicMeans );
        } catch ( GMapsGeneralException e ) {
            e.printStackTrace();
        }
        return ( combination != null ) ? combination.toString() : "NO_FEASIBLE_PATHS";
    }

    public String addBreakEvent ( long stTime, long endTime, long minInt )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        baseCaseConfiguration( true, true, true, true );
        //2018/01/20 h:10:00 - 11:00
        be1 = TestUtilities.setBreakEvent( 101, 1516442400, 1516446000, 30 * 60, user );
        be1.setScheduled( true );
        user.addBreak( be1 );
        //be2 is the break that we try to add
        be2 = TestUtilities.setBreakEvent( 102, stTime, endTime, minInt, user );
        be2.setScheduled( false );
        return ( scheduleManager.isBreakOverlapFreeIntoSchedule( be2, false ) ) ? "OK" : "NO";
    }

    public String swapEvents ( long stTime, long endTime, boolean breakEvent )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        baseCaseConfiguration( true, true, true, true );
        toe1 = TestUtilities.setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
        e4 = TestUtilities.setEvent( 4, stTime, endTime, true, false, lecco, maggianico, toe1, user );
        String msg = "";
        //2018/01/20 h:10:00 - 11:00
        if ( breakEvent ) {
            be1 = TestUtilities.setBreakEvent( 101, 1516442400, 1516446000, 30 * 60, user );
            be1.setScheduled( true );
            user.addBreak( be1 );
        }
        List < GenericEvent > swapResponse = null;
        try {
            swapResponse = pathManager.swapEvents( e4, privateMeans, publicMeans );
        } catch ( GMapsGeneralException e ) {
            e.printStackTrace();
        }
        for ( GenericEvent event : swapResponse ) {
            msg += event.toString() + "\n";
        }
        return msg;
    }

    public void baseCaseConfiguration ( boolean first, boolean second, boolean third, boolean setTravels )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        setBaseLocations();
        toe1 = TestUtilities.setTypeOfEvent( "test", ParamFirstPath.MIN_TIME );
        //2018/01/20 h:8:00 - 10:00
        e1 = TestUtilities.setEvent( 1, 1516435200, 1516442400, false, false,
                mandello, mandello, toe1, user );
        //2018/01/20 h:14:00 - 15:00
        e2 = TestUtilities.setEvent( 2, 1516456800, 1516460400, true, false,
                mandello, lecco, toe1, user );
        //2018/01/20 h:18:00 - 20:00
        e3 = TestUtilities.setEvent( 3, 1516471200, 1516478400, true, false,
                mandello, como, toe1, user );
        e1.setId( 1001 );
        e2.setId( 1002 );
        e3.setId( 1003 );
        setScheduleld( first, second, third );
        if ( setTravels ) {
            baseTravelsConfiguration();
        }
        ArrayList < Event > events = new ArrayList < Event >();
        events.add( e1 );
        events.add( e2 );
        events.add( e3 );
        user.setEvents( events );
        pathManager.setCurrentUser( user );
        setMeans( getMeansAL( privM ), getMeansAL( pubM ) );
    }

    public void baseTravelsConfiguration () {
        //2018/01/20 h:4:00 - 5:00
        t1 = TestUtilities.setTravel( 1516420800, 1516424400 );
        //t1.setRelatedEvent(e1);
        e1.setFeasiblePath( t1 );
        //2018/01/20 h:12:00 - 13:00
        t2 = TestUtilities.setTravel( 1516449600, 1516453200 );
        //t2.setRelatedEvent(e2);
        e2.setFeasiblePath( t2 );
        //2018/01/20 h:16:00 - 18:00
        t3 = TestUtilities.setTravel( 1516464000, 1516471200 );
        //t3.setRelatedEvent(e3);
        e3.setFeasiblePath( t3 );
    }

    public void setBaseLocations () throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        lecco = TestUtilities.setLocation( 45.8565698, 9.397670399999999 );
        mandello = TestUtilities.setLocation( 45.91386989999999, 9.317738499999999 );
        como = TestUtilities.setLocation( 45.8080597, 9.085176499999999 );
        maggianico = TestUtilities.setLocation( 45.8259029, 9.419594 );
        abbadia = TestUtilities.setLocation( 45.8948976, 9.336341900000001 );
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

    public void setScheduleld ( boolean first, boolean second, boolean third ) {
        e1.setScheduled( first );
        e2.setScheduled( second );
        e3.setScheduled( third );
    }

    public ArrayList < TravelMeanEnum > getMeansAL ( TravelMeanEnum[] list ) {
        ArrayList < TravelMeanEnum > tme = new ArrayList < TravelMeanEnum >();
        for ( TravelMeanEnum mean : list ) {
            tme.add( mean );
        }
        return tme;
    }

    public void setMeans ( ArrayList < TravelMeanEnum > privM, ArrayList < TravelMeanEnum > pubM ) {
        privateMeans = new ArrayList < TravelMeanEnum >( privM );
        publicMeans = new ArrayList < TravelMeanEnum >( pubM );
    }
}
