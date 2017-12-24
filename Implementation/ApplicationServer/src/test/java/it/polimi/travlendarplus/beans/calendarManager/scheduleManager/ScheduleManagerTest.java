package it.polimi.travlendarplus.beans.calendarManager.scheduleManager;

import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travels.Travel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class ScheduleManagerTest {
    Event event1 = new Event();
    Event event2 = new Event();
    Event event3 = new Event();
    Event event4 = new Event();
    Event event5 = new Event();
    BreakEvent break1 = new BreakEvent();
    BreakEvent break2 = new BreakEvent();
    User user = new User();
    ScheduleManager tester = new ScheduleManager();
    ScheduleManagerSettings setter = new ScheduleManagerSettings();


    @BeforeAll
    public void setUp () {
        setter.settings( this );
    }

    @Test
    public void areEventsOverlapFree () {
        assertEquals( false, tester.areEventsOverlapFree( event1, event2 ) );
        assertEquals( false, tester.areEventsOverlapFree( event1, event3 ) );
        assertEquals( true, tester.areEventsOverlapFree( event1, event4 ) );
        assertEquals( true, tester.areEventsOverlapFree( event2, event3 ) );
        assertEquals( true, tester.areEventsOverlapFree( event2, event4 ) );
        assertEquals( false, tester.areEventsOverlapFree( event3, event4 ) );
    }

    @Test
    public void isEventOverlapFreeIntoSchedule () {
        Event shortEvent = new Event();
        shortEvent.setStartingTime( Instant.ofEpochSecond( 11 ) );
        shortEvent.setEndingTime( Instant.ofEpochSecond( 12 ) );
        assertEquals( false, tester.isEventOverlapFreeIntoSchedule( event1, false ) );
        assertEquals( false, tester.isEventOverlapFreeIntoSchedule( event3, false ) );
        assertEquals( false, tester.isEventOverlapFreeIntoSchedule( event5, false ) );
        setter.noBreakEvents( this );
        assertEquals( true, tester.isEventOverlapFreeIntoSchedule( shortEvent, false ) );
        setter.settingOnlySetBreaks( this, 3 );
        assertEquals( false, tester.isEventOverlapFreeIntoSchedule( shortEvent, false ) );

    }


    @Test
    public void getSchedule () {
        assertEquals( 2, tester.getScheduleByDay( 0 ).getEvents().size() );
        assertEquals( 2, tester.getScheduleByDay( 13 ).getEvents().size() );
    }

    @Test
    public void isBreakOverlapFreeIntoSchedule () {
        assertEquals( true, tester.isBreakOverlapFreeIntoSchedule( break1, false ) );
        assertEquals( false, tester.isBreakOverlapFreeIntoSchedule( break2, false ) );
    }

    @Test
    public void getPossiblePreviousEvent () {
        Event ev1 = new Event();
        ev1.setStartingTime( Instant.ofEpochSecond( 3 ) );
        Event ev2 = new Event();
        ev2.setStartingTime( Instant.ofEpochSecond( 5 ) );
        Event ev3 = new Event();
        ev3.setStartingTime( Instant.ofEpochSecond( 50 ) );
        assertEquals( null, tester.getPossiblePreviousEvent( ev1.getStartingTime() ) );
        assertEquals( 4, tester.getPossiblePreviousEvent( ev2.getStartingTime() ).getStartingTime().getEpochSecond() );
        assertEquals( 14, tester.getPossiblePreviousEvent( ev3.getStartingTime() ).getStartingTime().getEpochSecond() );
    }

    @Test
    public void getPossibleFollowingEvent () {
        Event ev1 = new Event();
        ev1.setStartingTime( Instant.ofEpochSecond( 2 ) );
        Event ev2 = new Event();
        ev2.setStartingTime( Instant.ofEpochSecond( 7 ) );
        Event ev3 = new Event();
        ev3.setStartingTime( Instant.ofEpochSecond( 20 ) );
        assertEquals( 4, tester.getPossibleFollowingEvent( ev1.getStartingTime() ).getStartingTime().getEpochSecond() );
        assertEquals( 14, tester.getPossibleFollowingEvent( ev2.getStartingTime() ).getStartingTime().getEpochSecond() );
        assertEquals( null, tester.getPossibleFollowingEvent( ev3.getStartingTime() ) );
    }

    @Test
    public void getListWithNewEvent () {
        Event event = new Event();
        event.setStartingTime( Instant.ofEpochSecond( 5 ) );
        List < Event > updList = tester.getSchedule().getListWithNewEvent( event );
        assertEquals( 3, updList.size() );
        assertEquals( 5, updList.get( 1 ).getStartingTime().getEpochSecond() );
    }

    @Test
    public void getFeasiblePathCombinations () {
        setter.settingOnlySetBreaks( this, 2 );
        ArrayList < Travel > prev = setter.setPrevTravel();
        ArrayList < Travel > foll = setter.setFollTravel();
        Event event = new Event();
        event.setId( 6 );
        event.setStartingTime( Instant.ofEpochSecond( 12 ) );
        event.setEndingTime( Instant.ofEpochSecond( 13 ) );
        List < PathCombination > combs = tester.getFeasiblePathCombinations( event, prev, foll );
        assertEquals( 1, combs.size() );
        assertEquals( 11, combs.get( 0 ).getPrevPath().getStartingTime().getEpochSecond() );
    }

    @Test
    public void getFeasiblePathCombinations2 () {
        setter.settingOnlySetBreaks2( this );
        ArrayList < Travel > prev = setter.setPrevTravel2();
        Event event = new Event();
        event.setId( 6 );
        event.setStartingTime( Instant.ofEpochSecond( 25 ) );
        event.setEndingTime( Instant.ofEpochSecond( 26 ) );
        List < PathCombination > combs = tester.getFeasiblePathCombinations( event, prev, null );
        assertEquals( 2, combs.size() );
        assertEquals( 22, combs.get( 0 ).getPrevPath().getStartingTime().getEpochSecond() );
        assertEquals( 24, combs.get( 0 ).getPrevPath().getEndingTime().getEpochSecond() );
        assertEquals( 23, combs.get( 1 ).getPrevPath().getStartingTime().getEpochSecond() );
        assertEquals( 25, combs.get( 1 ).getPrevPath().getEndingTime().getEpochSecond() );

    }

}
