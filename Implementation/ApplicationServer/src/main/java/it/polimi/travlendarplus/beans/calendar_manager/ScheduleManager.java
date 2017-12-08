package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.PathCombination;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.ScheduleHolder;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.travels.Travel;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class ScheduleManager extends UserManager{

    final long DAILY_SECONDS_MINUS_ONE = 24*60*60-1;

    private ScheduleHolder schedule;
    //TODO (in path manager?) ensure relation event-path when an event is added (particular cases: first/last event)

    public ScheduleHolder getSchedule() {
        return schedule;
    }

    public ScheduleHolder getScheduleByDay(long day) {
        setSchedule(day);
        return schedule;
    }

    // This function is called before the calculation of possible paths for an added event.
    // Paths are not considered because the add of an event will cause the recalculation of involved paths.
    public boolean isEventOverlapFreeIntoSchedule(Event event, boolean forSwap) {
        if(!forSwap)
            setSchedule(event.getDayAtMidnight());
        //checking free overlapping with other events
        for(Event scheduledEvent: schedule.getEvents())
            if(!(areEventsOverlapFree(event, scheduledEvent)))
                return false;
        //checking feasibility of existing breaks
        for(BreakEvent scheduledBreak: schedule.getBreaks())
            if(!scheduledBreak.isMinimumEnsuredNoPathRegard(getEventsIntoInterval(schedule.getListWithNewEvent(event),
                    scheduledBreak)))
                return false;
        return true;
    }

    public boolean isBreakOverlapFreeIntoSchedule(BreakEvent breakEvent, boolean forSwap) {
        if(!forSwap)
            setSchedule(breakEvent.getDayAtMidnight());
        //new break cannot overlap with a previous break event
        for(BreakEvent scheduledBreak: schedule.getBreaks())
            if(!areEventsOverlapFree(breakEvent, scheduledBreak))
                return false;
        // Path duration is taken into account for check if a break can be added
        ArrayList<Event> involvedEvents = getEventsIntoIntervalWithPathRegard(schedule.getEvents(), breakEvent);
        return breakEvent.isMinimumEnsuredWithPathRegard(involvedEvents);
    }

    public void addToScheduled(Event event /*with best path*/) {
        //TODO
    }

    public void addToNotScheduled(Event event) {
        //TODO
    }

    //day represents the wanted day at 00:00:00
    public void setSchedule(long day) {
        ArrayList<Event> events = new ArrayList<Event>();
        ArrayList<BreakEvent> breaks = new ArrayList<BreakEvent>();
        Instant startingTime = Instant.ofEpochSecond(day);
        Instant endingTime = Instant.ofEpochSecond(day + DAILY_SECONDS_MINUS_ONE);

        //obtaining events into the schedule of the specified day
        for(Event event: getCurrentUser().getEvents())
            if(event.isScheduled() && event.getStartingTime().isAfter(startingTime) && event.getEndingTime().isBefore(endingTime))
                events.add(event);
        Collections.sort(events);
        //obtaining breaks into the schedule of the specified day
        for(BreakEvent breakEvent: getCurrentUser().getBreaks())
            if(breakEvent.isScheduled() && breakEvent.getStartingTime().isAfter(startingTime) &&
                    breakEvent.getEndingTime().isBefore(endingTime))
                breaks.add(breakEvent);
        Collections.sort(breaks);

        schedule = new ScheduleHolder(events, breaks);
    }

    //it returns null if the event would be the first of that day
    public Event getPossiblePreviousEvent (GenericEvent event) {
        for(int i=0; i<schedule.getEvents().size(); i++)
            if (event.getStartingTime().isBefore(schedule.getEvents().get(i).getStartingTime()))
                return i==0 ? null : schedule.getEvents().get(i-1);
        return (schedule.getEvents().size()==0) ? null : schedule.getEvents().get(schedule.getEvents().size()-1);
    }

    //it returns null if the event would be the last of that day
    public Event getPossibleFollowingEvent (GenericEvent event) {
        for(int i=0; i<schedule.getEvents().size(); i++)
            if(event.getStartingTime().isBefore(schedule.getEvents().get(i).getStartingTime()))
                return schedule.getEvents().get(i);
        return null;
    }

    //it returns true if gEvent1 and gEvent2 have not an overlap
    public boolean areEventsOverlapFree (GenericEvent event, GenericEvent scheduledEvent) {
        return event.getStartingTime().isAfter(scheduledEvent.getEndingTime()) ||
                event.getEndingTime().isBefore(scheduledEvent.getStartingTime());
    }

    //it returns a List of the events into "events" that overlap in the interval of "intervalEvent" .
    private ArrayList<Event> getEventsIntoInterval(List<Event> events, GenericEvent intervalEvent) {
        ArrayList<Event> involvedEvents = new ArrayList<Event>();
        for(Event event: events)
            if(event.getEndingTime().isAfter(intervalEvent.getStartingTime()) &&
                    event.getStartingTime().isBefore(intervalEvent.getEndingTime()))
                involvedEvents.add(event);
        return involvedEvents;
    }

    //it returns a List of the events into "events" that overlap in the interval of "intervalEvent" .
    //It considers also the event-related paths. The List must be ordered.
    private ArrayList<Event> getEventsIntoIntervalWithPathRegard(List<Event> events, GenericEvent intervalEvent) {
        ArrayList<Event> involvedEvents = new ArrayList<Event>();
        if(events.size()>0 && !areEventsOverlapFree(events.get(0), intervalEvent)) {
            involvedEvents.add(events.get(0));
            events.remove(0);
        }
        for(Event event: events)
            if(event.getEndingTime().isAfter(intervalEvent.getStartingTime()) &&
                    event.getFeasiblePath().getStartingTime().isBefore(intervalEvent.getEndingTime()))
                involvedEvents.add(event);
        return involvedEvents;
    }

    // This function is called after that all feasible previous and following paths are calculated.
    // It deterinates if a combination of a prev and a foll path is feasible according the scheduled breaks.
    public ArrayList<PathCombination> getFeasiblePathCombinations(Event event, ArrayList<Travel> previous, ArrayList<Travel> following) {
        ArrayList<PathCombination> feasibleComb = new ArrayList<PathCombination>();

        if(getPossiblePreviousEvent(event) == null)
            return getFeasiblePathCombinationsFirstEventCase(event, following);
        if(getPossibleFollowingEvent(event) == null)
            return getFeasiblePathCombinationsLastEventCase(event, previous);
        for(Travel prev: previous)
            for(Travel foll: following) {
                boolean ok = true;
                //analize the case of each combination of prev/foll path
                for (BreakEvent breakEvent : schedule.getBreaks()) {
                    ArrayList<Event> simul = schedule.getListWithNewEventPaths(prev, foll, event);
                    ArrayList<Event> involvSimul = getEventsIntoIntervalWithPathRegard(simul, breakEvent);
                    if (!breakEvent.isMinimumEnsuredWithPathRegard(involvSimul))
                        ok = false;
                }
                if (ok)
                    feasibleComb.add(new PathCombination(prev, foll));
            }

            return feasibleComb;
    }

    private ArrayList<PathCombination> getFeasiblePathCombinationsFirstEventCase(Event event, ArrayList<Travel> following) {
        ArrayList<PathCombination> feasibleComb = new ArrayList<PathCombination>();

        for(Travel foll: following) {
            boolean ok = true;
            for (BreakEvent breakEvent : schedule.getBreaks()) {
                ArrayList<Event> simul = schedule.getListWithNewEventPaths(null, foll, event);
                ArrayList<Event> involvSimul = getEventsIntoIntervalWithPathRegard(simul, breakEvent);
                if (!breakEvent.isMinimumEnsuredWithPathRegard(involvSimul))
                    ok = false;
            }
            if (ok)
                feasibleComb.add(new PathCombination(null, foll));
        }

        return feasibleComb;
    }

    private ArrayList<PathCombination> getFeasiblePathCombinationsLastEventCase(Event event, ArrayList<Travel> previous) {
        ArrayList<PathCombination> feasibleComb = new ArrayList<PathCombination>();

        for(Travel prev: previous) {
            boolean ok = true;
            for (BreakEvent breakEvent : schedule.getBreaks()) {
                ArrayList<Event> simul = schedule.getListWithNewEventPaths(prev, null, event);
                ArrayList<Event> involvSimul = getEventsIntoIntervalWithPathRegard(simul, breakEvent);
                if (!breakEvent.isMinimumEnsuredWithPathRegard(involvSimul))
                    ok = false;
            }
            if (ok)
                feasibleComb.add(new PathCombination(prev, null));
        }

        return feasibleComb;
    }

}
