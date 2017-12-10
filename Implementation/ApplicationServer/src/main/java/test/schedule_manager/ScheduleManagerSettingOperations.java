package test.schedule_manager;

import it.polimi.travlendarplus.beans.calendar_manager.ScheduleManager;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import java.time.Instant;
import java.util.ArrayList;

public class ScheduleManagerSettingOperations {

    public void settings(ScheduleManagerTest test) {
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<BreakEvent> breaks = new ArrayList<>();
        Travel travel2 = setTravel(2, 3);
        Travel travel4 = setTravel(8, 10);
        events.add(setEvent(test.event1, 2, 12, false));
        events.add(setEventWithPath(test.event2, 4, 8, true, travel2));
        events.add(setEvent(test.event3, 10, 16, false));
        events.add(setEventWithPath(test.event4, 14, 22, true, travel4));
        events.add(setEvent(test.event5, 21, 26, false));
        breaks.add(setBreakEvent(test.break1, 9, 15, 3, false));
        breaks.add(setBreakEvent(test.break2, 20, 23, 2, false));
        test.user.setEvents(events);
        test.user.setBreaks(breaks);

        test.tester.setCurrentUser(test.user);
        test.tester.setSchedule(0);
    }

    public void settingOnlySetBreaks(ScheduleManagerTest test) {
        ArrayList<BreakEvent> breaks = new ArrayList<>();
        breaks.add(setBreakEvent(test.break1, 9, 15, 3, true));
        breaks.add(setBreakEvent(test.break2, 20, 23, 2, false));
        test.tester.getCurrentUser().setBreaks(breaks);
        test.tester.setSchedule(0);
    }

    private Event setEvent(Event e, long stTime, long endTime, boolean scheduled) {
        e.setStartingTime(Instant.ofEpochSecond(stTime));
        e.setEndingTime(Instant.ofEpochSecond(endTime));
        e.setScheduled(scheduled);
        return e;
    }

    private Event setEventWithPath(Event e, long stTime, long endTime, boolean scheduled, Travel t) {
        e = setEvent(e, stTime, endTime, scheduled);
        e.setFeasiblePath(t);
        return e;
    }

    private BreakEvent setBreakEvent(BreakEvent e, long stTime, long endTime, int min, boolean scheduled) {
        e.setStartingTime(Instant.ofEpochSecond(stTime));
        e.setEndingTime(Instant.ofEpochSecond(endTime));
        e.setMinimumTime(min);
        e.setScheduled(scheduled);
        return e;
    }

    private Travel setTravel (long stTime, long endTime) {
        Travel travel = new Travel();
        TravelComponent travComp = new TravelComponent();
        travComp.setStartingTime(Instant.ofEpochSecond(stTime));
        travComp.setEndingTime(Instant.ofEpochSecond(endTime));
        ArrayList<TravelComponent> comps = new ArrayList<TravelComponent>();
        comps.add(travComp);
        travel.setMiniTravels(comps);
        return travel;
    }
}
