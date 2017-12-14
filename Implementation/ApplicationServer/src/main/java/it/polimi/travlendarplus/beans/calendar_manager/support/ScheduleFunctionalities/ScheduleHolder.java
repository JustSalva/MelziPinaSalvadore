package it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travels.Travel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleHolder {

    private List<Event> events;
    private List<BreakEvent> breaks;

    public ScheduleHolder(ArrayList<Event> events, ArrayList<BreakEvent> breaks) {
        this.events = events;
        this.breaks = breaks;
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public List<BreakEvent> getBreaks() {
        return Collections.unmodifiableList(breaks);
    }

    //the add of the event is only simulated: we use a copied ArrayList
    public ArrayList<Event> getListWithNewEvent(Event newEvent) {
        ArrayList<Event> newList = copyEventList();
        newList.add(newEvent);
        Collections.sort(newList);
        return newList;
    }

    //the event is really removed from the schedule
    public void removeSpecEvent(Event eventToRemove) {
        events.remove(eventToRemove);
    }

    //the event is really removed from the schedule
    public void removeSpecBreak(BreakEvent breakToRemove) {
        breaks.remove(breakToRemove);
    }

    // It returns a list of events composed by the scheduled events, the new event and the new event-related paths.
    public ArrayList<Event> getListWithNewEventPaths(Travel prev, Travel foll, Event event) {
        int i = 0;
        // The new event, with its related path, is added into the list of scheduled events.
        event.setFeasiblePath(prev);
        ArrayList<Event> newList = getListWithNewEvent(event);
        while(newList.get(i).getId() != event.getId() && i < newList.size())
            i++;
        // The following event is identified and the related path is setted.
        if(i < newList.size()-1)
            newList.get(i+1).setFeasiblePath(foll);
        return newList;
    }

    public boolean isLastEvent(Event event) {
        return !event.getStartingTime().isBefore(events.get(events.size()-1).getStartingTime());
    }

    private ArrayList<Event> copyEventList() {
        ArrayList<Event> newList = new ArrayList<Event> ();
        for(Event event: events)
            newList.add(event);
        return newList;
    }

}
