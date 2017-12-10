package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.PathCombination;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.ScheduleHolder;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.AlreadyScheduledException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

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

    // This function is called before the calculation of possible paths for an event that could be added.
    // Paths are not considered in this function because the addition of an event will cause the recalculation of involved paths.
    public boolean isEventOverlapFreeIntoSchedule(Event event, boolean forSwap) {
        if(!forSwap)
            setSchedule(event.getDayAtMidnight());
        // Checking free overlapping with scheduled events.
        for(Event scheduledEvent: schedule.getEvents())
            if(!(areEventsOverlapFree(event, scheduledEvent)))
                return false;
        // Checking if existing breaks will be still feasible with the addition of the new event.
        for(BreakEvent scheduledBreak: schedule.getBreaks())
            if(!scheduledBreak.isMinimumEnsuredNoPathRegard(getEventsIntoInterval(schedule.getListWithNewEvent(event),
                    scheduledBreak)))
                return false;
        return true;
    }

    // This function is called when a breakEvent could be added into the schedule.
    public boolean isBreakOverlapFreeIntoSchedule(BreakEvent breakEvent, boolean forSwap) {
        if(!forSwap)
            setSchedule(breakEvent.getDayAtMidnight());
        // In the system is not allowed the overlapping of two break events.
        for(BreakEvent scheduledBreak: schedule.getBreaks())
            if(!areEventsOverlapFree(breakEvent, scheduledBreak))
                return false;
        // Events and paths are taken into account in order to check if the minimum duration of a breakEvent can be ensured.
        ArrayList<Event> involvedEvents = getEventsIntoIntervalWithPathRegard(schedule.getEvents(), breakEvent);
        return breakEvent.isMinimumEnsuredWithPathRegard(involvedEvents);
    }

    public void addToScheduled(Event event /*with best path*/) {
        //TODO
    }

    public void addToNotScheduled(Event event) {
        //TODO
    }

    // Day parameter represents the wanted day at 00:00:00. This function is called in order to calculate the schedule
    // of a certain day and store it in schedule variable.
    public void setSchedule(long day) {
        ArrayList<Event> events = new ArrayList<Event>();
        ArrayList<BreakEvent> breaks = new ArrayList<BreakEvent>();
        Instant startingTime = Instant.ofEpochSecond(day);
        Instant endingTime = Instant.ofEpochSecond(day + DAILY_SECONDS_MINUS_ONE);
        // Obtaining events into the schedule of the specified day.
        for(Event event: getCurrentUser().getEvents())
            if(event.isScheduled() && !event.getStartingTime().isBefore(startingTime) && !event.getEndingTime().isAfter(endingTime))
                events.add(event);
        Collections.sort(events);
        // Obtaining breaks into the schedule of the specified day.
        for(BreakEvent breakEvent: getCurrentUser().getBreaks())
            if(breakEvent.isScheduled() && !breakEvent.getStartingTime().isBefore(startingTime) &&
                    !breakEvent.getEndingTime().isAfter(endingTime))
                breaks.add(breakEvent);
        Collections.sort(breaks);
        // The schedule is stored and can be used for next operations.
        schedule = new ScheduleHolder(events, breaks);
    }

    // It returns null if the event would be the first in the schedule of that day.
    public Event getPossiblePreviousEvent (GenericEvent event) {
        for(int i=0; i<schedule.getEvents().size(); i++)
            if (event.getStartingTime().isBefore(schedule.getEvents().get(i).getStartingTime()))
                return i==0 ? null : schedule.getEvents().get(i-1);
        return (schedule.getEvents().size()==0) ? null : schedule.getEvents().get(schedule.getEvents().size()-1);
    }

    // It returns null if the event would be the last in the schedule of that day.
    public Event getPossibleFollowingEvent (GenericEvent event) {
        for(int i=0; i<schedule.getEvents().size(); i++)
            if(event.getStartingTime().isBefore(schedule.getEvents().get(i).getStartingTime()))
                return schedule.getEvents().get(i);
        return null;
    }

    // It returns true if the two events are not overlapping.
    public boolean areEventsOverlapFree (GenericEvent event, GenericEvent scheduledEvent) {
        return !event.getStartingTime().isBefore(scheduledEvent.getEndingTime()) ||
                !event.getEndingTime().isAfter(scheduledEvent.getStartingTime());
    }

    // It returns an ArrayList of events into "events" that are overlapping with "intervalEvent" .
    // Possible paths are not taken into account.
    private ArrayList<Event> getEventsIntoInterval(List<Event> events, GenericEvent intervalEvent) {
        ArrayList<Event> involvedEvents = new ArrayList<Event>();
        for(Event event: events)
            if(event.getEndingTime().isAfter(intervalEvent.getStartingTime()) &&
                    event.getStartingTime().isBefore(intervalEvent.getEndingTime()))
                involvedEvents.add(event);
        return involvedEvents;
    }

    // It returns an ArrayList of events into "events" that are overlapping with "intervalEvent" .
    // It considers also the event-related paths. The ArrayList passed as a param must be ordered.
    private ArrayList<Event> getEventsIntoIntervalWithPathRegard(List<Event> events, GenericEvent intervalEvent) {
        ArrayList<Event> involvedEvents = new ArrayList<Event>();
        // Event-related paths are taken into account in order to check if there is an overlap.
        for(Event event: events)
            if (event.getEndingTime().isAfter(intervalEvent.getStartingTime()) &&
                    event.getFeasiblePath().getStartingTime().isBefore(intervalEvent.getEndingTime()))
                involvedEvents.add(event);
        return involvedEvents;
    }

    // This function is called after that all feasible previous and following paths are calculated.
    // The two ArrayList passed as params contain possible travels that don't overlap with prev/foll events.
    // It determines if a combination of prev and foll path is feasible according to the scheduled breaks.
    public ArrayList<PathCombination> getFeasiblePathCombinations(Event event, ArrayList<Travel> previous, ArrayList<Travel> following) {
        ArrayList<PathCombination> feasibleComb = new ArrayList<PathCombination>();
        /*if(getPossiblePreviousEvent(event) == null)
            return getFeasiblePathCombinationsFirstEventCase(event, following);*/
        if(getPossibleFollowingEvent(event) == null)
            return getFeasiblePathCombinationsLastEventCase(event, previous);
        for(Travel prev: previous)
            for(Travel foll: following) {
                boolean combinationFeasible = true;
                // Analyzing for each combination of prev/foll paths if it would be feasible with each scheduled break.
                for (BreakEvent breakEvent : schedule.getBreaks()) {
                    ArrayList<Event> simulList = schedule.getListWithNewEventPaths(prev, foll, event);
                    ArrayList<Event> simulInvolved = getEventsIntoIntervalWithPathRegard(simulList, breakEvent);
                    // simulInvolved contains events that happen in breakEvent interval.
                    if (!breakEvent.isMinimumEnsuredWithPathRegard(simulInvolved))
                        combinationFeasible = false;
                }
                if (combinationFeasible)
                    feasibleComb.add(new PathCombination(prev, foll));
            }

            return feasibleComb;
    }

    // It is used when the event to be added into the schedule would be the last: it doesn't have a following path.
    private ArrayList<PathCombination> getFeasiblePathCombinationsLastEventCase(Event event, ArrayList<Travel> previous) {
        ArrayList<PathCombination> feasibleComb = new ArrayList<PathCombination>();
        for(Travel prev: previous) {
            boolean combinationFeasible = true;
            // Analyzing for each prev path if it would be feasible with each scheduled break.
            for (BreakEvent breakEvent : schedule.getBreaks()) {
                ArrayList<Event> simulList = schedule.getListWithNewEventPaths(prev, null, event);
                ArrayList<Event> simulInvolved = getEventsIntoIntervalWithPathRegard(simulList, breakEvent);
                if (!breakEvent.isMinimumEnsuredWithPathRegard(simulInvolved))
                    combinationFeasible = false;
            }
            if (combinationFeasible)
                feasibleComb.add(new PathCombination(prev, null));
        }

        return feasibleComb;
    }

    public ScheduleHolder swapEvents( long  idForcedEvent ) throws EntityNotFoundException, AlreadyScheduledException{
        Event forced = Event.load( idForcedEvent ); //TODO forse qui è meglio chiamare la funzione di EventManager
        if ( forced.isScheduled()  ){
            throw new AlreadyScheduledException( );
        }
        //TODO da DD non è da mettere qui?
        return null;
    }

}
