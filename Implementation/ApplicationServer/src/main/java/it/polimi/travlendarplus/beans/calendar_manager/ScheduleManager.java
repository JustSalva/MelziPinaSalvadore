package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.travels.Travel;

import javax.ejb.Stateless;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class ScheduleManager extends UserManager{

    final long DAILY_SECONDS_MINUS_ONE = 24*60*60-1;

    List<GenericEvent> schedule = new ArrayList<GenericEvent>();

    public List<GenericEvent> getScheduleByDay(long day) {
        setSchedule(day);
        return schedule;
    }

    public List<Event> swapEvents(long idEvent) {
        // queries that act on swapIn and swapOut events
        //TODO
        return null;
    }

    //before calculate possible paths for a given event. Check happens between starting/ending time of event
    //and starting/ending time of events and travels into the schedule
    public boolean checkOverlapIntoSchedule(Event event) {
        setSchedule(event.getDayAtMidnight());
        return false;
    }

    public List<Travel> checkFeasibility (Event event, ArrayList<Travel> possiblePaths) {
        return null;
    }

    public void addToScheduled(Event event /*with best path*/) {

    }

    public void addToNotScheduled(Event event) {

    }

    //day represents the wanted day at 00:00:00
    private void setSchedule(long day) {
        Instant startingTime = Instant.ofEpochSecond(day);
        Instant endingTime = Instant.ofEpochSecond(day + DAILY_SECONDS_MINUS_ONE);
        for(Event event: getCurrentUser().getEvents())
            if(event.isScheduled() && event.getStartingTime().isAfter(startingTime) && event.getEndingTime().isBefore(endingTime))
                schedule.add(event);
        Collections.sort(schedule);
    }

    /*public boolean isScheduleFeasible(long day) {
        setDayByMidnight(day);
        setSchedule();
        return schedule.isFeasible();
    }*/

    //returns true if gEvent1 and gEvent2
    private boolean areEventsOverlapFree (GenericEvent event, GenericEvent scheduledEvent) {
        return event.getStartingTime().isAfter(scheduledEvent.getEndingTime()) ||
                event.getEndingTime().isBefore(scheduledEvent.getStartingTime());
    }

    private boolean isEventOverlapFreeRespectTravel (GenericEvent event, GenericEvent travelRelatedEvent) {
        //travel is the path related to gEvent2 (it happens before gEvent2)
        Travel travel = travelRelatedEvent.getFeasiblePath();

        //event after travel or before event previous to travel
        return event.getStartingTime().isAfter(travel.getMiniTravels().get(travel.getMiniTravels().size()-1).getEndingTime()) ||
                travelRelatedEvent.getEndingTime().isBefore(getPreviousIntoSchedule(travelRelatedEvent).getStartingTime());
    }

    private GenericEvent getPreviousIntoSchedule (GenericEvent gEvent) {
        for(int i=0; i<schedule.size(); i++)
            if(gEvent.getId() == schedule.get(i).getId())
                return i==0 ? schedule.get(i) : schedule.get(i-1);
        return null; //TODO exception
    }

}
