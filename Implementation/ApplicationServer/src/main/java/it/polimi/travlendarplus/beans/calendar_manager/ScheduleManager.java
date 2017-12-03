package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.entities.calendar.Event;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.List;

@Stateless
public class ScheduleManager extends UserManager{
    /*
    checkOverlap(event, user) used during the creaton of an event
    addToNotScheduled(event, user)
    addToScheduled(event, bestPath, user)
    checkFeasibility(event, paths, user)
    isScheduleFeasible(user)
    isBreakEventScheduled(breakEvent, user)

    functions related to:
    obtain daily schedule
    swap events
    obtain previous event
     */

    private Long startingTime;
    private Long endingTime;
    private List<Event> schedule;
    private Event eventToSwapIn;
    private List<Event> eventToSwapOut;

    public void setTime(Long startingTime) {
        this.startingTime = startingTime;
        this.endingTime = startingTime + 24*60*59;
    }

    public List<Event> getSchedule() {
        setSchedule();
        return Collections.unmodifiableList(schedule);
    }

    private void setSchedule() {
        String query = "SELECT * FROM Event WHERE User = "+getCurrentUser().getEmail()+
                " AND starting time ecc"+startingTime+endingTime;
        // *execute query and handle results *
        this.schedule = schedule;
    }

    public void setEventToSwapIn(Event eventToSwapIn) {
        this.eventToSwapIn = eventToSwapIn;
        setEventToSwapOut();
    }

    private void setEventToSwapOut() {
        //query based on EventToSwapIn
    }

    public List<Event> swapEvents() {
        // queries that act on swapIn and swapOut events
        setTime(eventToSwapIn.calculateDate());
        return getSchedule();
    }

    //TODO decide if for any operation we want attributes automatically updated or we have to call the relative function in order to obtain it
}
