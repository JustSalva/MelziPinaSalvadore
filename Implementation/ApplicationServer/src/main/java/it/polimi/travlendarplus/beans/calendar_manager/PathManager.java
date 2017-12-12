package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsException.GMapsGeneralException;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsJSONReader;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsDirectionsHandler;
import it.polimi.travlendarplus.beans.calendar_manager.support.HTMLCallAndResponse;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.PathCombination;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.ScheduleHolder;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import org.json.JSONException;
import org.json.JSONObject;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PathManager extends UserManager{

    @EJB
    ScheduleManager scheduleManager;

    @PostConstruct
    public void postConstruct() {
        scheduleManager.setCurrentUser(this.getCurrentUser());
    }

    //attention to last event of the schedule (only one array of paths)

    public PathCombination calculatePath(Event event, ArrayList<TravelMeanEnum> privateMeans,
                                         ArrayList<TravelMeanEnum> publicMeans, boolean forSwap) {
        if(!forSwap)
            scheduleManager.setSchedule(event.getDayAtMidnight());
        // Obtaining possible paths according to previous and following scheduled events.
        ArrayList<Travel> previousPaths = getPreviousTravels(event, privateMeans, publicMeans);
        ArrayList<Travel> followingPaths = getFollowingTravels(event, privateMeans, publicMeans);

        // Selecting only combinations of paths that ensure feasibility for each scheduled break event.
        ArrayList<PathCombination> possibleCombinations = scheduleManager.getFeasiblePathCombinations(event,
                previousPaths, followingPaths);

        //TODO preferences on this ArrayList
        //TODO best path among which that remain (return it)

        return possibleCombinations.get(0);
    }

    private ArrayList<Travel> getPreviousTravels (Event event, List<TravelMeanEnum> privateMeans,
                                                  List<TravelMeanEnum> publicMeans) {
        ArrayList<Travel> possiblePaths = new ArrayList<Travel>();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        Event previous = scheduleManager.getPossiblePreviousEvent(event);
        try {
            // Obtaining baseCall string for previous paths, here locations and times are setted.
            String baseCall = directionsHandler.getBaseCallPreviousPath(event, previous);
            boolean sameLoc = isBetweenSameLocations(event);
            // Obtaining possible paths for the specified means.
            possiblePaths = possiblePathsAdder(baseCall, privateMeans, publicMeans, previous, event, sameLoc);
        } catch (GMapsGeneralException e) {
            e.printStackTrace();
        }
        return possiblePaths;
    }

    private ArrayList<Travel> getFollowingTravels (Event event, List<TravelMeanEnum> privateMeans,
                                                   List<TravelMeanEnum> publicMeans) {
        ArrayList<Travel> possiblePaths = new ArrayList<Travel>();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        Event following = scheduleManager.getPossibleFollowingEvent(event);
        if(following == null)
            // The event would be the last of that day. An empty ArrayList is returned.
            return possiblePaths;
        try {
            // Obtaining baseCall string for previous paths, here locations and times are setted.
            String baseCall = directionsHandler.getBaseCallFollowingPath(event, following);
            boolean sameLoc = isBetweenSameLocations(following);
            // Obtaining possible paths for the specified means.
            possiblePaths = possiblePathsAdder(baseCall, privateMeans, publicMeans, event, following, sameLoc);
        } catch (GMapsGeneralException e) {
            e.printStackTrace();
        }
        return possiblePaths;
    }

    private ArrayList<Travel> possiblePathsAdder(String baseCall, List<TravelMeanEnum> privateMeans,
                    List<TravelMeanEnum> publicMeans, Event eventA, Event eventB, boolean sameLoc) throws GMapsGeneralException{
        ArrayList<Travel> possiblePaths = new ArrayList<Travel>();
        privatePathsHandler(possiblePaths, baseCall, eventA, eventB, privateMeans, sameLoc);
        publicPathsHandler(possiblePaths, baseCall, eventB, publicMeans, sameLoc);
        return possiblePaths;
    }

    private void privatePathsHandler(List<Travel> possiblePaths, String baseCall, Event eventA, Event eventB,
                                 List<TravelMeanEnum> privateMeans, boolean sameLoc) throws GMapsGeneralException {
        GMapsJSONReader reader = new GMapsJSONReader();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        if(sameLoc)
            privateMeans = privateMeansSameLoc(privateMeans);
        for(TravelMeanEnum mean: privateMeans) {
            JSONObject privatePathJSON = HTMLCallAndResponse.performCall(directionsHandler.getCallWithNoTransit(baseCall, mean));
            ArrayList<Travel> privatePaths = new ArrayList<Travel>();
            try {
                //Two cases: 1) eventA is the previous and eventB the main event -> it is managed the case in which eventA is NULL.
                //2) eventA is the main event and eventB is the following event -> eventB is not NULL because this case is managed above.
                privatePaths = (eventA != null) ?
                    reader.getTravelNoTransitMeans(privatePathJSON, mean, eventA.getEndingTime().getEpochSecond(),
                        true, eventB.getDeparture(), eventB.getEventLocation()) :
                    // It is the case when the possible new event would be the first in the schedule.
                    reader.getTravelNoTransitMeans(privatePathJSON, mean, eventB.getStartingTime().getEpochSecond(),
                        false, eventB.getDeparture(), eventB.getEventLocation());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Considering only paths that allow to attend to the event in time
            for(Travel travel: privatePaths)
                if(!travel.getEndingTime().isAfter(eventB.getStartingTime()))
                    possiblePaths.add(travel);
        }
    }

    private void publicPathsHandler(List<Travel> possiblePaths, String baseCall, Event eventB,
                                    List<TravelMeanEnum> publicMeans, boolean sameLoc) throws GMapsGeneralException {
        GMapsJSONReader reader = new GMapsJSONReader();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        if(!sameLoc) { // If departure location is the same of arrival location, the path can be done only with private means.
            JSONObject publicPathsJSON = HTMLCallAndResponse.performCall(directionsHandler.getCallByTransit(baseCall, publicMeans));
            ArrayList<Travel> publicPaths = new ArrayList<Travel>();
            try {
                publicPaths = reader.getTravelWithTransitMeans(publicPathsJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (Travel travel : publicPaths)
                if (travel.getEndingTime().isBefore(eventB.getStartingTime()))
                    possiblePaths.add(travel);
        }
    }

    private boolean isBetweenSameLocations(Event event) {   
        return event.getDeparture().getLatitude() == event.getEventLocation().getLatitude() &&
                event.getDeparture().getLongitude() == event.getEventLocation().getLongitude();
    }

    public ScheduleHolder swapEvents(Event forcedEvent, ArrayList<TravelMeanEnum> privateMeans,
                                     ArrayList<TravelMeanEnum> publicMeans) {
        scheduleManager.setSchedule(forcedEvent.getDayAtMidnight());
        List<Event> swapOutEvents = new ArrayList<Event>();
        List<BreakEvent> swapOutBreaks = new ArrayList<BreakEvent>();
        List<PathCombination> combs = new ArrayList<PathCombination>();
        firstSwapPhase(forcedEvent);
        // Calculating prev/foll path for the forcedEvent.
        boolean complete = false;
        while(!complete && !scheduleManager.getSchedule().getEvents().isEmpty()) {
            swapOutEvents = new ArrayList<Event>();
            swapOutBreaks = new ArrayList<BreakEvent>();
            List<Travel> prev = getPreviousTravels(forcedEvent, privateMeans, publicMeans);
            List<Travel> foll = getFollowingTravels(forcedEvent, privateMeans, publicMeans);
            if(!prev.isEmpty() && (!foll.isEmpty() || scheduleManager.getSchedule().isLastScheduledEvent(forcedEvent))) {
                combs = scheduleManager.getFeasiblePathCombinations(forcedEvent, prev, foll);
                //TODO apply user preferences on combs
                complete = !combs.isEmpty();
                if(!complete)
                    for(BreakEvent breakScheduled: scheduleManager.getSchedule().getBreaks())
                        if(!scheduleManager.areEventsOverlapFree(forcedEvent, breakScheduled))
                            swapOutBreaks.add(breakScheduled);
            }
            else if(prev.isEmpty() && scheduleManager.getPossiblePreviousEvent(forcedEvent) != null)
                swapOutEvents.add(scheduleManager.getPossiblePreviousEvent(forcedEvent));
            else if(foll.isEmpty() && scheduleManager.getPossibleFollowingEvent(forcedEvent) != null)
                swapOutEvents.add(scheduleManager.getPossibleFollowingEvent(forcedEvent));
            removeEvents(swapOutEvents, swapOutBreaks);
        }
        
        //TODO update DB with swapOUT events, swapOUT breaks and swapIN
        return scheduleManager.getSchedule();
    }

    // Removing events that overlap with forcedEvent
    private void firstSwapPhase(Event forcedEvent) {
        ArrayList<Event> swapOutEvents = new ArrayList<Event>();
        for (Event event : scheduleManager.getSchedule().getEvents())
            if (!scheduleManager.areEventsOverlapFree(event, forcedEvent))
                swapOutEvents.add(event);
        for (Event event : swapOutEvents)
            scheduleManager.getSchedule().removeSpecEvent(event);
    }

    private void removeEvents(List<Event> swapOutEvents, List<BreakEvent> swapOutBreaks){
        for(Event event: swapOutEvents)
            scheduleManager.getSchedule().removeSpecEvent(event);
        for(BreakEvent breakEvent: swapOutBreaks)
            scheduleManager.getSchedule().removeSpecBreak(breakEvent);
    }

    private ArrayList<TravelMeanEnum> privateMeansSameLoc(List<TravelMeanEnum> privateMeans) {
        ArrayList<TravelMeanEnum> copy = new ArrayList<TravelMeanEnum>();
        for(TravelMeanEnum mean: privateMeans)
            if(!mean.getParam().equals("driving"))
                copy.add(mean);
        if(!copy.contains(TravelMeanEnum.BY_FOOT))
            copy.add(TravelMeanEnum.BY_FOOT);
        return copy;
    }

    @Override
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        this.scheduleManager.setCurrentUser(currentUser);
    }

}
