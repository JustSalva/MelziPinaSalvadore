package it.polimi.travlendarplus.beans.calendarManager.support;

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

import static org.junit.jupiter.api.Assertions.*;

@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class ScheduleHolderTest {
    Event e1, e2, e3;
    BreakEvent b1, b2;
    User user;
    ScheduleHolder schHold;

    @BeforeAll
    void init () {
        e1 = new Event( "e1", Instant.ofEpochSecond( 100 ), Instant.ofEpochSecond( 200 ), true,
                "", false, true, null, null, null, null );
        e2 = new Event( "e2", Instant.ofEpochSecond( 300 ), Instant.ofEpochSecond( 400 ), true,
                "", true, true, null, null, null, null );
        e3 = new Event( "e3", Instant.ofEpochSecond( 600 ), Instant.ofEpochSecond( 700 ), true,
                "", true, true, null, null, null, null );
        e1.setId( 1 );
        e2.setId( 2 );
        e3.setId( 3 );
        ArrayList < Event > events = new ArrayList < Event >();
        events.add( e1 );
        events.add( e2 );
        events.add( e3 );
        b1 = new BreakEvent( "b1", Instant.ofEpochSecond( 200 ), Instant.ofEpochSecond( 400 ), true, 50 );
        b2 = new BreakEvent( "b2", Instant.ofEpochSecond( 400 ), Instant.ofEpochSecond( 600 ), true, 100 );
        ArrayList < BreakEvent > breakk = new ArrayList < BreakEvent >();
        breakk.add( b1 );
        breakk.add( b2 );
        schHold = new ScheduleHolder( events, breakk );
    }

    @Test
    void getEvents () {
        List < Event > events = schHold.getEvents();
        assertEquals( 3, events.size() );
    }

    @Test
    void getBreaks () {
        List < BreakEvent > breaks = schHold.getBreaks();
        assertEquals( 2, breaks.size() );
    }

    @Test
    void getListWithNewEvent () {
        Event e4 = new Event( "e3", Instant.ofEpochSecond( 500 ), Instant.ofEpochSecond( 550 ), true,
                "", true, true, null, null, null, null );
        e4.setId( 4 );
        List<Event> list1 = schHold.getListWithNewEvent( e4, false );
        assertEquals( 4, list1.size() );
        assertEquals( 100, list1.get( 0 ).getStartingTime().getEpochSecond() );
        assertEquals( 300, list1.get( 1 ).getStartingTime().getEpochSecond() );
        assertEquals( 500, list1.get( 2 ).getStartingTime().getEpochSecond() );
        assertEquals( 600, list1.get( 3 ).getStartingTime().getEpochSecond() );
        List<Event> list2 = schHold.getListWithNewEvent( e3, true );
        assertEquals( 3, list2.size() );
        assertEquals( 100, list2.get( 0 ).getStartingTime().getEpochSecond() );
        assertEquals( 300, list2.get( 1 ).getStartingTime().getEpochSecond() );
        assertEquals( 600, list2.get( 2 ).getStartingTime().getEpochSecond() );
    }

    @Test
    void removeSpecEvent () {
        init();
        schHold.removeSpecEvent( e1 );
        assertEquals( 2, schHold.getEvents().size() );
        init();
        schHold.removeSpecEvent( new Event() );
        assertEquals( 3, schHold.getEvents().size() );
    }

    @Test
    void removeSpecBreak () {
        init();
        schHold.removeSpecBreak( b1 );
        assertEquals( 1, schHold.getBreaks().size() );
    }

    @Test
    void getListWithNewEventPaths () {
        Travel prev = new Travel();
        Travel foll = new Travel();
        init();
        //general case
        List < Event > list1 = schHold.getListWithNewEventPaths( prev, foll, e2 );
        assertTrue( list1.get( 0 ).getFeasiblePath() == null );
        assertFalse( list1.get( 1 ).getFeasiblePath() == null );
        assertFalse( list1.get( 2 ).getFeasiblePath() == null );
        init();
        //first event
        List < Event > list2 = schHold.getListWithNewEventPaths( prev, foll, e1 );
        assertFalse( list2.get( 0 ).getFeasiblePath() == null );
        assertFalse( list2.get( 1 ).getFeasiblePath() == null );
        assertTrue( list2.get( 2 ).getFeasiblePath() == null );
        init();
        //last event
        List < Event > list3 = schHold.getListWithNewEventPaths( prev, foll, e3 );
        assertTrue( list3.get( 0 ).getFeasiblePath() == null );
        assertTrue( list3.get( 1 ).getFeasiblePath() == null );
        assertFalse( list3.get( 2 ).getFeasiblePath() == null );
    }

    @Test
    void isLastEvent () {
        init();
        assertFalse( schHold.isLastEvent( e1 ) );
        assertFalse( schHold.isLastEvent( e2 ) );
        assertTrue( schHold.isLastEvent( e3 ) );
    }

}