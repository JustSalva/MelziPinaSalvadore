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
    private List<Event> eventToSwapOut;
    private Event eventToSwapIn;

    public void setStartingTime(Long startingTime) {
        this.startingTime = startingTime;
        this.endingTime = startingTime + 24*60*60*999;
    }

    public List<Event> getSchedule() {
        return Collections.unmodifiableList(schedule);
    }

    public void setSchedule() {
        String query = "SELECT * FROM Event WHERE User = "+getCurrentUser().getEmail()+
                " AND starting time ecc";
        this.schedule = schedule;
    }
}
