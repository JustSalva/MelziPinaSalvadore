package it.polimi.travlendarplus.beans.calendarManager.scheduleManager;

import it.polimi.travlendarplus.TestUtilities;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travelMeans.PrivateTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import java.time.Instant;
import java.util.ArrayList;

public class ScheduleManagerSettings {

    public void settings ( ScheduleManagerTest test ) {
        ArrayList < Event > events = new ArrayList <>();
        ArrayList < BreakEvent > breaks = new ArrayList <>();
        Travel travel2 = TestUtilities.setTravel( 2, 3 );
        Travel travel4 = TestUtilities.setTravel( 8, 10 );
        events.add( setEvent( test.event1, 1, 2, 12, false ) );
        events.add( setEventWithPath( test.event2, 2, 4, 8, true, travel2 ) );
        events.add( setEvent( test.event3, 3, 10, 16, false ) );
        events.add( setEventWithPath( test.event4, 4, 14, 22, true, travel4 ) );
        events.add( setEvent( test.event5, 5, 21, 26, false ) );
        breaks.add( setBreakEvent( test.break1, 11, 9, 15, 2, false ) );
        breaks.add( setBreakEvent( test.break2, 12, 20, 23, 2, false ) );
        test.user.setEvents( events );
        test.user.setBreaks( breaks );

        test.tester.setCurrentUser( test.user );
        test.tester.setSchedule( Instant.ofEpochSecond( 20 ), 20 );
    }

    public void noBreakEvents ( ScheduleManagerTest test ) {
        ArrayList < BreakEvent > breaks = new ArrayList <>();
        breaks.add( setBreakEvent( test.break1, 11, 9, 15, 3, false ) );
        breaks.add( setBreakEvent( test.break2, 12, 20, 23, 2, false ) );
        test.tester.getCurrentUser().setBreaks( breaks );
        test.tester.setSchedule( Instant.ofEpochSecond( 20 ), 20 );
    }

    public void settingOnlySetBreaks ( ScheduleManagerTest test, int time ) {
        ArrayList < BreakEvent > breaks = new ArrayList <>();
        breaks.add( setBreakEvent( test.break1, 11, 9, 15, time, true ) );
        breaks.add( setBreakEvent( test.break2, 12, 20, 23, 2, false ) );
        test.tester.getCurrentUser().setBreaks( breaks );
        test.tester.setSchedule( Instant.ofEpochSecond( 20 ), 20 );
    }

    public void settingOnlySetBreaks2 ( ScheduleManagerTest test ) {
        ArrayList < BreakEvent > breaks = new ArrayList <>();
        breaks.add( setBreakEvent( test.break1, 11, 9, 15, 2, false ) );
        breaks.add( setBreakEvent( test.break2, 12, 20, 26, 1, true ) );
        test.tester.getCurrentUser().setBreaks( breaks );
        test.tester.setSchedule( Instant.ofEpochSecond( 20 ), 20 );
    }

    private Event setEvent ( Event e, long id, long stTime, long endTime, boolean scheduled ) {
        e.setId( id );
        e.setStartingTime( Instant.ofEpochSecond( stTime ) );
        e.setEndingTime( Instant.ofEpochSecond( endTime ) );
        e.setScheduled( scheduled );
        return e;
    }

    private Event setEventWithPath ( Event e, long id, long stTime, long endTime, boolean scheduled, Travel t ) {
        e = setEvent( e, id, stTime, endTime, scheduled );
        e.setFeasiblePath( t );
        return e;
    }

    private BreakEvent setBreakEvent ( BreakEvent e, long id, long stTime, long endTime, int min, boolean scheduled ) {
        e.setId( id );
        e.setStartingTime( Instant.ofEpochSecond( stTime ) );
        e.setEndingTime( Instant.ofEpochSecond( endTime ) );
        e.setMinimumTime( min );
        e.setScheduled( scheduled );
        return e;
    }

    public ArrayList < Travel > setPrevTravel () {
        ArrayList < Travel > prev = new ArrayList < Travel >();
        Travel t1 = TestUtilities.setTravel( 10, 12 );
        Travel t2 = TestUtilities.setTravel( 11, 12 );
        addCarMean( t1 );
        addCarMean( t2 );
        prev.add( t1 );
        prev.add( t2 );
        return prev;
    }

    public ArrayList < Travel > setFollTravel () {
        ArrayList < Travel > foll = new ArrayList < Travel >();
        Travel t1 = TestUtilities.setTravel( 13, 14 );
        addCarMean( t1 );
        foll.add( t1 );
        return foll;
    }

    public ArrayList < Travel > setPrevTravel2 () {
        ArrayList < Travel > prev = new ArrayList < Travel >();
        Travel t1 = TestUtilities.setTravel( 22, 25 );
        Travel t2 = TestUtilities.setTravel( 22, 24 );
        Travel t3 = TestUtilities.setTravel( 23, 25 );
        prev.add( t1 );
        prev.add( t2 );
        prev.add( t3 );
        return prev;
    }

    private void addCarMean ( Travel t ) {
        for ( TravelComponent tComp : t.getMiniTravels() ) {
            tComp.setMeanUsed( new PrivateTravelMean( "car", TravelMeanEnum.CAR, 0 ) );
        }
    }
}
