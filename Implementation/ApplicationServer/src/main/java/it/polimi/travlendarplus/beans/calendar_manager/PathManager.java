package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsException.GMapsGeneralException;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsJSONReader;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsDirectionsHandler;
import it.polimi.travlendarplus.beans.calendar_manager.support.HTMLCallAndResponse;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.PathCombination;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.ScheduleHolder;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travelMeans.TravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PathManager extends UserManager{

    @EJB
    ScheduleManager scheduleManager;

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
            // Obtaining possible paths for the specified means.
            possiblePaths = possiblePathsAdder(baseCall, privateMeans, publicMeans, previous, event);
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
            // Obtaining possible paths for the specified means.
            possiblePaths = possiblePathsAdder(baseCall, privateMeans, publicMeans, event, following);
        } catch (GMapsGeneralException e) {
            e.printStackTrace();
        }
        return possiblePaths;
    }

    //Two cases: 1) eventA is the previous and eventB the main event -> it is managed the case in which eventA is NULL.
    //2) eventA is the main event and eventB is the following event -> eventB is not NULL because this case is managed above.
    private ArrayList<Travel> possiblePathsAdder(String baseCall, List<TravelMeanEnum> privateMeans,
                    List<TravelMeanEnum> publicMeans, Event eventA, Event eventB) throws GMapsGeneralException{
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        GMapsJSONReader reader = new GMapsJSONReader();
        ArrayList<Travel> possiblePaths = new ArrayList<>();
        // Calculating paths with private travel means.
        for(TravelMeanEnum mean: privateMeans) {
            JSONObject privatePathJSON = HTMLCallAndResponse.performCall(directionsHandler.getCallWithNoTransit(baseCall, mean));
            ArrayList<Travel> privatePaths;
            if(eventA != null)
                privatePaths = reader.getTravelNoTransitMeans(privatePathJSON, mean,
                    eventA.getEndingTime().getEpochSecond(), true, eventA.getEventLocation(), eventB.getEventLocation());
            else // It is the case when the possible new event would be the first in the schedule.
                privatePaths = reader.getTravelNoTransitMeans(privatePathJSON, mean,
                        eventB.getStartingTime().getEpochSecond(), false, eventB.getDeparture(), eventB.getEventLocation());
            // Considering only paths that allow to attend to the event in time
            for(Travel travel: privatePaths)
                if(!travel.getEndingTime().isAfter(eventB.getStartingTime()))
                    possiblePaths.add(travel);
        }
        // Calculating paths with public travel means.
        JSONObject publicPathsJSON = HTMLCallAndResponse.performCall(directionsHandler.getCallByTransit(baseCall, publicMeans));
        ArrayList<Travel> publicPaths = reader.getTravelWithTransitMeans(publicPathsJSON);
        for(Travel travel: publicPaths)
            if(travel.getEndingTime().isBefore(eventB.getStartingTime()))
                possiblePaths.add(travel);

        return possiblePaths;
    }


    public static void main (String[] a) {
        GMapsDirectionsHandler gMapsURL = new GMapsDirectionsHandler();
        JSONObject response = HTMLCallAndResponse.performCall(gMapsURL.getBaseCallPreviousPath(null, null));
        GMapsJSONReader responseReader = new GMapsJSONReader();
        //System.out.println(responseReader.getTravelNoTransitMeans(response, TravelMeanEnum.CAR, 1512558191, null, null));
        try {
            System.out.println(responseReader.getTravelWithTransitMeans(response));
        } catch (GMapsGeneralException e) {
            e.printStackTrace();
        }
        //System.out.println(response);
    }

    public ScheduleHolder swapEvents(Event forcedEvent) {
        scheduleManager.setSchedule(forcedEvent.getDayAtMidnight());
        List<Event> swapOutEvents = new ArrayList<Event>();
        List<BreakEvent> swapOutBreaks = new ArrayList<BreakEvent>();
        List<PathCombination> combs = new ArrayList<PathCombination>();
        List<TravelMeanEnum> privateMeans = new ArrayList<TravelMeanEnum>();
        List<TravelMeanEnum> publicMeans = new ArrayList<TravelMeanEnum>();
        firstSwapPhase(forcedEvent);
        // Calculating prev/foll path for the forcedEvent.
        //TODO get user preferences or specify ALL travel means (some solutions will be removed after whith preferences check)
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

        /*PathCombination comb = calculatePath(forcedEvent, null, null, true);
        boolean first = scheduleManager.getPossiblePreviousEvent(forcedEvent) == null;
        boolean last = scheduleManager.getPossibleFollowingEvent(forcedEvent) == null;
        while(!accettablePathCombination(first, last, comb)) {
            //removing a break that can overlap or prev/foll overlapping events
            //TODO check if there is a break overlapping
            for(BreakEvent breakScheduled: scheduleManager.getSchedule().getBreaks())
                if(!scheduleManager.areEventsOverlapFree(forcedEvent, breakScheduled))
                    swapOutBreaks.add(breakScheduled);

            if(swapOutBreaks.size()>0)
                scheduleManager.getSchedule().removeSpecBreak(swapOutBreaks.get(0));
            else {
                if(!scheduleManager.areEventsOverlapFree(forcedEvent, scheduleManager.getPossiblePreviousEvent(forcedEvent)))
                    scheduleManager.getSchedule().removeSpecEvent(scheduleManager.getPossiblePreviousEvent(forcedEvent));
                if(!scheduleManager.areEventsOverlapFree(forcedEvent, scheduleManager.getPossibleFollowingEvent(forcedEvent)))
                    scheduleManager.getSchedule().removeSpecEvent(scheduleManager.getPossibleFollowingEvent(forcedEvent));
            }
            //TODO get user preferences or specify ALL travel means (some solutions will be removed after whith preferences check)
            comb = calculatePath(forcedEvent, null, null, true);
            first = scheduleManager.getPossiblePreviousEvent(forcedEvent) == null;
            last = scheduleManager.getPossibleFollowingEvent(forcedEvent) == null;
        }*/

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

    /*private boolean accettablePathCombination(boolean first, boolean last, PathCombination comb) {
        if(first && last)
            return true;
        if(first)
            return comb.getFollPath() != null;
        if(last)
            return comb.getPrevPath() != null;
        return comb.getPrevPath() != null && comb.getFollPath() != null;
    }*/

}
