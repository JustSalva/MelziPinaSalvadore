package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsException.GMapsGeneralException;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsJSONReader;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsDirectionsHandler;
import it.polimi.travlendarplus.beans.calendar_manager.support.HTMLCallAndResponse;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.PathCombination;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.ScheduleHolder;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PathManager extends UserManager{

    @EJB
    ScheduleManager scheduleManager;

    //TODO calculate path before and after
    //attention to first and last event of the schedule (only one array of paths)

    public PathCombination calculatePath(Event event, ArrayList<TravelMeanEnum> privateMeans,
                                         ArrayList<TravelMeanEnum> publicMeans, boolean forSwap) {

        if(!forSwap)
            scheduleManager.setSchedule(event.getDayAtMidnight());
        ArrayList<Travel> previousPaths = getPreviousTravels(event, privateMeans, publicMeans);
        ArrayList<Travel> followingPaths = getFollowingTravels(event, privateMeans, publicMeans);

        ArrayList<PathCombination> possibleCombinations = scheduleManager.getFeasiblePathCombinations(event,
                previousPaths, followingPaths);

        //TODO preferences on this ArrayList
        //TODO best path among which that remain (return it)

        return null;
    }

    private ArrayList<Travel> getPreviousTravels (Event event, ArrayList<TravelMeanEnum> privateMeans,
                                                  ArrayList<TravelMeanEnum> publicMeans) {
        ArrayList<Travel> possiblePaths = new ArrayList<Travel>();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        Event previous = scheduleManager.getPossiblePreviousEvent(event);
        if(previous == null)
            return possiblePaths;

        try {
            String baseCall = directionsHandler.getBaseCallPreviousPath(event, previous);
            possiblePaths = possiblePathsAdder(baseCall, privateMeans, publicMeans, previous, event);
        } catch (GMapsGeneralException e) {
            e.printStackTrace();
        }

        return possiblePaths;
    }

    private ArrayList<Travel> getFollowingTravels (Event event, ArrayList<TravelMeanEnum> privateMeans,
                                                  ArrayList<TravelMeanEnum> publicMeans) {
        ArrayList<Travel> possiblePaths = new ArrayList<Travel>();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        Event following = scheduleManager.getPossibleFollowingEvent(event);
        if(following == null)
            return possiblePaths;

        try {
            String baseCall = directionsHandler.getBaseCallFollowingPath(event, following);
            possiblePaths = possiblePathsAdder(baseCall, privateMeans, publicMeans, event, following);
        } catch (GMapsGeneralException e) {
            e.printStackTrace();
        }

        return possiblePaths;
    }

    private ArrayList<Travel> possiblePathsAdder(String baseCall, ArrayList<TravelMeanEnum> privateMeans,
                    ArrayList<TravelMeanEnum> publicMeans, Event eventA, Event eventB) throws GMapsGeneralException{
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        GMapsJSONReader reader = new GMapsJSONReader();
        ArrayList<Travel> possiblePaths = new ArrayList<>();

        //adding private travels
        ArrayList<JSONObject> privatePathsJSON = new ArrayList<JSONObject>();
        for(TravelMeanEnum mean: privateMeans) {
            JSONObject privatePathJSON = HTMLCallAndResponse.performCall(directionsHandler.getCallWithNoTransit(baseCall, mean));
            ArrayList<Travel> privatePaths = reader.getTravelNoTransitMeans(privatePathJSON, mean,
                    eventA.getEndingTime().getEpochSecond(), eventA.getEventLocation(), eventB.getEventLocation());
            for(Travel travel: privatePaths)
                if(travel.getEndingTime().isBefore(eventB.getStartingTime()))
                    possiblePaths.add(travel);
        }
        //adding public travels
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
        //removing events that overlap with forcedEvent
        for (Event event : scheduleManager.getSchedule().getEvents())
            if (!scheduleManager.areEventsOverlapFree(event, forcedEvent))
                swapOutEvents.add(event);
        for (Event event : swapOutEvents)
            scheduleManager.getSchedule().removeSpecEvent(event);
        //calculating prev/foll path for the forcedEvent
        //TODO get user preferences or specify ALL travel means (some solutions will be removed after whith preferences check)
        PathCombination comb = calculatePath(forcedEvent, null, null, true);
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
        }
        //TODO update DB with swapOUT events, swapOUT breaks and swapIN
        return scheduleManager.getSchedule();
    }

    private boolean accettablePathCombination(boolean first, boolean last, PathCombination comb) {
        if(first && last)
            return true;
        if(first)
            return comb.getFollPath() != null;
        if(last)
            return comb.getPrevPath() != null;
        return comb.getPrevPath() != null && comb.getFollPath() != null;
    }

}
