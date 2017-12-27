package it.polimi.travlendarplus.beans.calendarManager.support;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travels.Travel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleHolder {

    private List < Event > events;
    private List < BreakEvent > breaks;

    public ScheduleHolder ( ArrayList < Event > events, ArrayList < BreakEvent > breaks ) {
        this.events = events;
        this.breaks = breaks;
    }

    public List < Event > getEvents () {
        return Collections.unmodifiableList( events );
    }

    public List < BreakEvent > getBreaks () {
        return Collections.unmodifiableList( breaks );
    }

    /**
     * Calculates the schedule after the addition of a new event.
     *
     * @param newEvent event to add in the copy list
     * @return a sorted copy of the list of events with the new event passed as a parameter.
     */
    public List < Event > getListWithNewEvent ( Event newEvent ) {
        List < Event > newList = copyEventList();
        newList = newList.stream().filter( e -> e.getId() != newEvent.getId() ).collect( Collectors
                .toCollection( ArrayList::new ) );
        newList.add( newEvent );
        Collections.sort( newList );
        return newList;
    }

    /**
     * Removes an event from the schedule.
     *
     * @param eventToRemove event to remove.
     */
    public void removeSpecEvent ( Event eventToRemove ) {
        events.remove( eventToRemove );
    }

    /**
     * Removes a break from the schedule.
     *
     * @param breakToRemove break to remove.
     */
    public void removeSpecBreak ( BreakEvent breakToRemove ) {
        if ( breakToRemove != null ) {
            breaks.remove( breakToRemove );
        }
    }

    /**
     * Calculates the schedule after the addition of a new event, with path adjusting.
     *
     * @param prev  previous path.
     * @param foll  following path.
     * @param event event to add in the schedule.
     * @return list of events composed by the scheduled events, the new event and the new event-related paths.
     */
    public List < Event > getListWithNewEventPaths ( Travel prev, Travel foll, Event event ) {
        int i = 0;
        // The new event, with its related path, is added into the list of scheduled events.
        event.setFeasiblePath( prev );
        List < Event > newList = getListWithNewEvent( event );
        while ( newList.get( i ).getId() != event.getId() && i < newList.size() ) {
            i++;
        }
        // The following event is identified and the related path is setted.
        if ( i < newList.size() - 1 ) {
            newList.get( i + 1 ).setFeasiblePath( foll );
        }
        return newList;
    }

    /**
     * States if it is the last event into the schedule.
     *
     * @param event event to analize.
     * @return true if it is the last event in the schedule.
     */
    public boolean isLastEvent ( Event event ) {
        return !event.getStartingTime().isBefore( events.get( events.size() - 1 ).getStartingTime() );
    }


    /**
     * @return a copy list of the schedule
     */
    private ArrayList < Event > copyEventList () {
        ArrayList < Event > newList = new ArrayList < Event >();
        for ( Event event : events ) {
            newList.add( event );
        }
        return newList;
    }

    /**
     * Changes the path for the specified event.
     *
     * @param id   id of the event of which change the path.
     * @param path path to assign.
     */
    public void changeEventPath ( long id, Travel path ) {
        for ( Event e : events ) {
            if ( e.getId() == id ) {
                e.setFeasiblePath( path );
            }
        }
    }

    public void setEvents ( List < Event > events ) {
        this.events = events;
    }

    public void setBreaks ( List < BreakEvent > breaks ) {
        this.breaks = breaks;
    }
}
