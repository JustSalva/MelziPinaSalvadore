package it.polimi.travlendarplus.beans.calendar_manager.support;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;

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

    public ArrayList<Event> getListWithNewEvent(Event newEvent) {
        ArrayList<Event> newList = new ArrayList<Event> ();
        for(Event event: events)
            newList.add(event);
        newList.add(newEvent);
        Collections.sort(newList);
        return newList;
    }
}
