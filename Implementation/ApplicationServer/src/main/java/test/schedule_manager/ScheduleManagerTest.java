package test.schedule_manager;

import it.polimi.travlendarplus.beans.calendar_manager.ScheduleManager;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    ScheduleManagerSettingOperations setter = new ScheduleManagerSettingOperations();


    @BeforeAll
    public void setUp() {
        setter.settings(this);
    }

    @Test
    public void areEventsOverlapFree() {
        assertEquals(false, tester.areEventsOverlapFree(event1, event2));
        assertEquals(false, tester.areEventsOverlapFree(event1, event3));
        assertEquals(true, tester.areEventsOverlapFree(event1, event4));
        assertEquals(true, tester.areEventsOverlapFree(event2, event3));
        assertEquals(true, tester.areEventsOverlapFree(event2, event4));
        assertEquals(false, tester.areEventsOverlapFree(event3, event4));
    }

    @Test
    public void isEventOverlapFreeIntoSchedule() {
        assertEquals(false, tester.isEventOverlapFreeIntoSchedule(event1,false));
        assertEquals(false, tester.isEventOverlapFreeIntoSchedule(event3, false));
        assertEquals(false, tester.isEventOverlapFreeIntoSchedule(event5, false));
    }

    @Test
    public void getSchedule() {
        assertEquals(2, tester.getScheduleByDay(0).getEvents().size());
        assertEquals(1, tester.getScheduleByDay(5).getEvents().size());
    }

    @Test
    public void isBreakOverlapFreeIntoSchedule() {
        assertEquals(true, tester.isBreakOverlapFreeIntoSchedule(break1, false));
        assertEquals(false, tester.isBreakOverlapFreeIntoSchedule(break2, false));
    }

    @Test
    public void getPossiblePreviousEvent() {
        Event ev1 = new Event();
        ev1.setStartingTime(Instant.ofEpochSecond(3));
        Event ev2 = new Event();
        ev2.setStartingTime(Instant.ofEpochSecond(5));
        Event ev3 = new Event();
        ev3.setStartingTime(Instant.ofEpochSecond(50));
        assertEquals(null, tester.getPossiblePreviousEvent(ev1));
        assertEquals(4, tester.getPossiblePreviousEvent(ev2).getStartingTime().getEpochSecond());
        assertEquals(14, tester.getPossiblePreviousEvent(ev3).getStartingTime().getEpochSecond());
    }

    @Test
    public void getPossibleFollowingEvent() {
        Event ev1 = new Event();
        ev1.setStartingTime(Instant.ofEpochSecond(2));
        Event ev2 = new Event();
        ev2.setStartingTime(Instant.ofEpochSecond(7));
        Event ev3 = new Event();
        ev3.setStartingTime(Instant.ofEpochSecond(20));
        assertEquals(4, tester.getPossibleFollowingEvent(ev1).getStartingTime().getEpochSecond());
        assertEquals(14, tester.getPossibleFollowingEvent(ev2).getStartingTime().getEpochSecond());
        assertEquals(null, tester.getPossibleFollowingEvent(ev3));
    }



}
