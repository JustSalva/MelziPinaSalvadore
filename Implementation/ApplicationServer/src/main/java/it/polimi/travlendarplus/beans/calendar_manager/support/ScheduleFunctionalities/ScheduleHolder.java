package it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
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
    public void removeSpecEvent(GenericEvent eventToRemove) {
        events.remove(eventToRemove);
    }

    private ArrayList<Event> copyEventList() {
        ArrayList<Event> newList = new ArrayList<Event> ();
        for(Event event: events)
            newList.add(event);
        return newList;
    }

    public ArrayList<Event> getListWithNewEventPaths(Travel prev, Travel foll, Event event) {
        event.setFeasiblePath(prev);
        ArrayList<Event> newList = getListWithNewEvent(event);
        int i = 0;
        while(newList.get(i).getId() != event.getId())
            i++;
        if(i < newList.size()-1)
            newList.get(i+1).setFeasiblePath(foll);
        return newList;
    }
}
