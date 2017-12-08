package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsException.GMapsGeneralException;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsJSONReader;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsDirectionsHandler;
import it.polimi.travlendarplus.beans.calendar_manager.support.HTMLCallAndResponse;
import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.PathCombination;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;

@Stateless
public class PathManager extends UserManager{

    @EJB
    ScheduleManager scheduleManager;

    //TODO calculate path before and after
    //attention to first and last event of the schedule (only one array of paths)

    public PathCombination calculatePath(Event event, ArrayList<TravelMeanEnum> privateMeans,
                                         ArrayList<TravelMeanEnum> publicMeans) {

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

    //((Event)scheduleManager.getPossibleFollowingEvent(event)) to use for second parameter in function: baseCallFollowingPath()



}
