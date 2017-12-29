package it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.BadRequestException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsUnavailableException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.LocationNotFoundException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GmapsDirectionsAndJsonTest {
    String[] locations = { "London,England", "Milano,Italy", "Paris,France", "Monza,Italy", "Rho,Italy",
            "Sesto San Giovanni,Italy" };
    List < Location > loc = new ArrayList < Location >();
    TravelMeanEnum[] pubMeans = { TravelMeanEnum.SUBWAY, TravelMeanEnum.BUS, TravelMeanEnum.TRAIN, TravelMeanEnum.TRAM };
    TravelMeanEnum[] priMeans = { TravelMeanEnum.BY_FOOT, TravelMeanEnum.CAR, TravelMeanEnum.BIKE };
    List < TravelMeanEnum > pubAL, priAL;
    // several hours in 20th of January
    long[] times = { 1516435200, 1516442400, 1516449600, 1516456800, 1516464000 };
    List < Instant > instantTimes = new ArrayList < Instant >();
    Random randomGenerator = new Random();
    GMapsDirectionsHandler gmdh = new GMapsDirectionsHandler();
    GMapsJSONReader jsonReader = new GMapsJSONReader();
    Event ev, prev, foll;

    @Before
    public void init () throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        /*for ( String name : locations )
            loc.add( GMapsGeocoder.getLocationByString( name ) );*/
        loc.add( new Location( 51.5074, 0.1278, "London,England" ) );
        loc.add( new Location( 45.4642, 9.1900, "Milan,Italy" ) );
        loc.add( new Location( 48.8566, 2.3522, "Paris,France" ) );
        loc.add( new Location( 45.5845, 9.2744, "Monza,Italy" ) );
        loc.add( new Location( 45.5326, 9.0396, "Rho,Italy" ) );
        loc.add( new Location( 45.5328, 9.2257, "Sesto San Giovanni,Italy" ) );
        for ( long time : times ) {
            instantTimes.add( Instant.ofEpochSecond( time ) );
        }
    }

    @Test
    public void check () throws GMapsGeneralException {
        assertEquals( locations.length, loc.size() );
        loc = loc.stream().filter( l -> l.getAddress().length() > 0 ).collect( Collectors
                .toCollection( ArrayList::new ) );
        assertEquals( locations.length, loc.size() );
        assertEquals( times.length, instantTimes.size() );

        ArrayList < Travel > prevTravels = new ArrayList < Travel >();
        ArrayList < Travel > follTravels = new ArrayList < Travel >();

        setTravelMeans();
        setEvents();
        String baseCallPrev = gmdh.getBaseCallPreviousPath( ev, prev, true );
        for ( TravelMeanEnum priM : priAL ) {
            JSONObject response = HTMLCallAndResponse.performCall( gmdh.getCallWithNoTransit( baseCallPrev, priM ) );
            System.out.println( gmdh.getCallWithNoTransit( baseCallPrev, priM ) );
            for ( Travel t : jsonReader.getTravelNoTransitMeans( response, priM, ( ev.isTravelAtLastChoice() ) ?
                            ev.getStartingTime().getEpochSecond() : prev.getEndingTime().getEpochSecond(),
                    !ev.isTravelAtLastChoice(), ev.getDeparture(), ev.getEventLocation() ) ) {
                prevTravels.add( t );
            }
        }

        JSONObject response = HTMLCallAndResponse.performCall( gmdh.getCallByTransit( baseCallPrev, pubAL ) );
        System.out.println( gmdh.getCallByTransit( baseCallPrev, pubAL ) );
        for ( Travel t : jsonReader.getTravelWithTransitMeans( response ) ) {
            prevTravels.add( t );
        }

        if ( foll != null ) {
            gmdh = new GMapsDirectionsHandler();
            String baseCallFoll = gmdh.getBaseCallFollowingPath( ev, foll, true );
            for ( TravelMeanEnum priM : priAL ) {
                response = HTMLCallAndResponse.performCall( gmdh.getCallWithNoTransit( baseCallFoll, priM ) );
                System.out.println( gmdh.getCallWithNoTransit( baseCallFoll, priM ) );
                for ( Travel t : jsonReader.getTravelNoTransitMeans( response, priM, ( foll.isTravelAtLastChoice() )
                                ? foll.getStartingTime().getEpochSecond()
                                : ev.getEndingTime().getEpochSecond(), !foll.isTravelAtLastChoice(),
                        ( foll.isPrevLocChoice() ) ? ev.getEventLocation() : foll.getDeparture(), foll.getEventLocation() ) ) {
                    follTravels.add( t );
                }
            }

            if ( !sameLocation( foll.getDeparture(), foll.getEventLocation() ) ) {
                response = HTMLCallAndResponse.performCall( gmdh.getCallByTransit( baseCallFoll, pubAL ) );
                System.out.println( gmdh.getCallByTransit( baseCallFoll, pubAL ) );
                for ( Travel t : jsonReader.getTravelWithTransitMeans( response ) ) {
                    follTravels.add( t );
                }
            }
        }

        for ( Travel t : prevTravels ) {
            if ( ev.isPrevLocChoice() && prev != null ) {
                assertTrue( sameLocation( t.getMiniTravels().get( 0 ).getDeparture(), prev.getEventLocation() ) );
            } else if ( ev.isPrevLocChoice() && prev == null ) {
                assertTrue( sameLocation( t.getMiniTravels().get( 0 ).getDeparture(), ev.getEventLocation() ) );
            } else {
                assertTrue( sameLocation( t.getMiniTravels().get( 0 ).getDeparture(), ev.getDeparture() ) );
            }
            if ( ev.isTravelAtLastChoice() ) {
                assertTrue( !t.getEndingTime().isAfter( ev.getStartingTime() ) );
            } else if ( prev != null ) {
                assertTrue( !t.getStartingTime().isBefore( prev.getEndingTime() ) );
            }
        }

        for ( Travel t : follTravels ) {
            if ( foll.isPrevLocChoice() ) {
                assertTrue( sameLocation( t.getMiniTravels().get( 0 ).getDeparture(), ev.getEventLocation() ) );
            } else {
                assertTrue( sameLocation( t.getMiniTravels().get( 0 ).getDeparture(), foll.getDeparture() ) );
            }
            if ( foll.isTravelAtLastChoice() ) {
                assertTrue( !t.getEndingTime().isAfter( foll.getStartingTime() ) );
            } else {
                assertTrue( !t.getStartingTime().isBefore( ev.getEndingTime() ) );
            }
        }

    }

    private void setTravelMeans () {
        priAL = new ArrayList < TravelMeanEnum >();
        pubAL = new ArrayList < TravelMeanEnum >();
        priAL.add( priMeans[ 2 ] );
        pubAL.add( pubMeans[ randomGenerator.nextInt( 4 ) ] );
    }

    private void setEvents () {
        ev = new Event();

        boolean depPrevLoc = randomGenerator.nextInt( 2 ) == 1;
        boolean atLast = randomGenerator.nextInt( 2 ) == 1;
        ev.setPrevLocChoice( depPrevLoc );
        ev.setTravelAtLastChoice( atLast );
        Location evLocat = loc.get( randomGenerator.nextInt( locations.length ) );
        ev.setDeparture( loc.get( randomGenerator.nextInt( locations.length ) ) );
        ev.setEventLocation( evLocat );
        ev.setStartingTime( instantTimes.get( randomGenerator.nextInt( times.length ) ) );
        ev.setEndingTime( instantTimes.get( randomGenerator.nextInt( times.length ) ) );

        if ( depPrevLoc ) {
            prev = new Event();
            Location prevLocat = loc.get( randomGenerator.nextInt( locations.length ) );
            prev.setEventLocation( prevLocat );
            ev.setDeparture( prevLocat );
            prev.setEndingTime( instantTimes.get( randomGenerator.nextInt( times.length ) ) );
        } else {
            ev.setTravelAtLastChoice( true );
        }

        if ( true /*randomGenerator.nextInt( 2 ) == 1*/ ) {
            foll = new Event();
            depPrevLoc = randomGenerator.nextInt( 2 ) == 1;
            atLast = randomGenerator.nextInt( 2 ) == 1;
            foll.setPrevLocChoice( depPrevLoc );
            foll.setTravelAtLastChoice( atLast );
            foll.setDeparture( ( depPrevLoc ) ? evLocat : loc.get( randomGenerator.nextInt( locations.length ) ) );
            foll.setEventLocation( loc.get( randomGenerator.nextInt( locations.length ) ) );
            foll.setStartingTime( instantTimes.get( randomGenerator.nextInt( times.length ) ) );
        }

    }

    private boolean sameLocation ( Location l1, Location l2 ) {
        return Math.abs( l1.getLatitude() - l2.getLatitude() ) < 0.01 && Math.abs( l1.getLongitude() -
                l2.getLongitude() ) < 0.01;
    }
}
