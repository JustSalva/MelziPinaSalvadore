package it.polimi.travlendarplus;

import it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities.GMapsGeocoder;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.BadRequestException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsUnavailableException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.LocationNotFoundException;

import java.time.Instant;
import java.util.ArrayList;

public class TestUtilities {

    public static Event setEvent ( long id, long stTime, long endTime, boolean prevLoc, boolean travelAtLast,
                                   Location dep, Location arr, TypeOfEvent toe, User user ) {
        Event e = new Event();
        e.setName( "T+" );
        e.setId( id );
        e.setStartingTime( Instant.ofEpochSecond( stTime ) );
        e.setEndingTime( Instant.ofEpochSecond( endTime ) );
        e.setPrevLocChoice( prevLoc );
        e.setTravelAtLastChoice( travelAtLast );
        e.setDeparture( dep );
        e.setEventLocation( arr );
        e.setType( toe );
        setUser( user );
        e.setUser( user );
        //user.save();
        //e.save();
        return e;
    }

    public static void setUser ( User user ) {
        user.setEmail( "test" );
    }

    public static BreakEvent setBreakEvent ( long id, long stTime, long endTime, long minTime, User user ) {
        BreakEvent br = new BreakEvent();
        br.setName( "T+break" );
        br.setId( id );
        br.setStartingTime( Instant.ofEpochSecond( stTime ) );
        br.setEndingTime( Instant.ofEpochSecond( endTime ) );
        br.setMinimumTime( minTime );
        setUser( user );
        br.setUser( user );
        //user.save();
        //br.save();
        return br;
    }

    public static Travel setTravel ( long stTime, long endTime ) {
        Travel t = new Travel();
        ArrayList < TravelComponent > compList = new ArrayList < TravelComponent >();
        TravelComponent comp = new TravelComponent();
        comp.setStartingTime( Instant.ofEpochSecond( stTime ) );
        comp.setEndingTime( Instant.ofEpochSecond( endTime ) );
        compList.add( comp );
        t.setMiniTravels( compList );
        return t;
    }

    public static Location setLocation ( double lat, double lng )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        return GMapsGeocoder.getLocationObject( lat, lng );
    }


    public static TypeOfEvent setTypeOfEvent ( String name, ParamFirstPath param ) {
        TypeOfEvent toe = new TypeOfEvent();
        toe.setName( name );
        toe.setParamFirstPath( param );
        //toe.save();
        return toe;
    }

}
