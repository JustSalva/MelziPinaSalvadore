package it.polimi.travlendarplus.beans.calendarManager;

import it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities.GMapsDirectionsHandler;
import it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities.GMapsJSONReader;
import it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities.HTMLCallAndResponse;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
public class PathManager extends UserManager {

    @EJB
    ScheduleManager scheduleManager;
    @EJB
    PreferenceManager preferenceManager;

    @PostConstruct
    public void postConstruct() {
        scheduleManager.setCurrentUser( currentUser );
        preferenceManager.setCurrentUser( currentUser );
    }

    public PathCombination calculatePath( Event event, ArrayList< TravelMeanEnum > privateMeans,
                                          ArrayList< TravelMeanEnum > publicMeans ) {
        scheduleManager.setSchedule( event.getStartingTime(), ScheduleManager.SECONDS_IN_A_DAY );
        // Obtaining possible paths that don't overlap with previous and following scheduled events.
        List< Travel > previousPaths = getPreviousTravels( event, privateMeans, publicMeans );
        List< Travel > followingPaths = getFollowingTravels( event, privateMeans, publicMeans );
        // Filtering obtained paths according to constraints defined
        previousPaths = previousPaths.stream().filter( p -> preferenceManager.checkConstraints( p, event.getType() ) )
                .collect( Collectors.toList() );
        followingPaths = followingPaths.stream().filter( p -> preferenceManager.checkConstraints( p, event.getType() ) )
                .collect( Collectors.toList() );
        // Selecting only combinations of paths that ensure feasibility for each scheduled break event.
        ArrayList< PathCombination > possibleCombinations = scheduleManager.getFeasiblePathCombinations( event,
                previousPaths, followingPaths );
        return ( !possibleCombinations.isEmpty() ) ? preferenceManager.findBestpath(
                possibleCombinations, event.getType() )
                : null;
    }

    private ArrayList< Travel > getPreviousTravels( Event event, List< TravelMeanEnum > privateMeans,
                                                    List< TravelMeanEnum > publicMeans ) {
        ArrayList< Travel > possiblePaths = new ArrayList< Travel >();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        Event previous = scheduleManager.getPossiblePreviousEvent( event.getStartingTime() );
        if ( event.isPrevLocChoice() )
            event.setDeparture( previous.getEventLocation() );
        try {
            // Obtaining baseCall string for previous paths, here locations and times are setted.
            String baseCall = directionsHandler.getBaseCallPreviousPath( event, previous );
            boolean sameLoc = isBetweenSameLocations( event );
            // Obtaining possible paths for the specified means.
            possiblePaths = possiblePathsAdder( baseCall, privateMeans, publicMeans, previous, event, sameLoc );
        } catch ( GMapsGeneralException err ) {
            Logger.getLogger( PathManager.class.getName() ).log( Level.SEVERE, err.getMessage(), err );
        }
        return possiblePaths;
    }

    private ArrayList< Travel > getFollowingTravels( Event event, List< TravelMeanEnum > privateMeans,
                                                     List< TravelMeanEnum > publicMeans ) {
        ArrayList< Travel > possiblePaths = new ArrayList< Travel >();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        Event following = scheduleManager.getPossibleFollowingEvent( event.getStartingTime() );
        if ( following == null )
            // The event would be the last of that day. An empty ArrayList is returned.
            return possiblePaths;
        try {
            if ( following.isPrevLocChoice() ) {
                following.setDeparture( event.getEventLocation() );
            }
            // Obtaining baseCall string for previous paths, here locations and times are setted.
            String baseCall = directionsHandler.getBaseCallFollowingPath( event, following );
            boolean sameLoc = isBetweenSameLocations( following );
            // Obtaining possible paths for the specified means.
            possiblePaths = possiblePathsAdder( baseCall, privateMeans, publicMeans, event, following, sameLoc );
        } catch ( GMapsGeneralException err ) {
            Logger.getLogger( PathManager.class.getName() ).log( Level.SEVERE, err.getMessage(), err );
        }
        return possiblePaths;
    }

    private ArrayList< Travel > possiblePathsAdder( String baseCall, List< TravelMeanEnum > privateMeans,
                                                    List< TravelMeanEnum > publicMeans, Event eventA, Event eventB,
                                                    boolean sameLoc )
            throws GMapsGeneralException {
        ArrayList< Travel > possiblePaths = new ArrayList< Travel >();
        if ( !privateMeans.isEmpty() ) {
            privatePathsHandler( possiblePaths, baseCall, eventA, eventB, privateMeans, sameLoc );
        }
        if ( !publicMeans.isEmpty() ) {
            publicPathsHandler( possiblePaths, baseCall, eventA, eventB, publicMeans, sameLoc );
        }
        return possiblePaths;
    }

    private void privatePathsHandler( List< Travel > possiblePaths, String baseCall, Event eventA, Event eventB,
                                      List< TravelMeanEnum > privateMeans, boolean sameLoc )
            throws GMapsGeneralException {
        GMapsJSONReader reader = new GMapsJSONReader();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        if ( sameLoc )
            privateMeans = privateMeansSameLoc( privateMeans );
        for ( TravelMeanEnum mean : privateMeans ) {
            ArrayList< Travel > privatePaths = new ArrayList< Travel >();
            try {
                JSONObject privatePathJSON = HTMLCallAndResponse.performCall(
                        directionsHandler.getCallWithNoTransit( baseCall, mean ) );
                /*Two cases: 1) eventA is the previous and eventB the main event -> it is managed the case
                in which eventA is NULL.
                2) eventA is the main event and eventB is the following event -> eventB is not NULL
                 because this case is managed above.*/
                privatePaths = ( eventA != null && !eventB.isTravelAtLastChoice() ) ?
                        reader.getTravelNoTransitMeans( privatePathJSON, mean, eventA.getEndingTime().getEpochSecond(),
                                true, eventB.getDeparture(), eventB.getEventLocation() ) :
                        // It is the case when the possible new event would be the first in the schedule.
                        reader.getTravelNoTransitMeans( privatePathJSON, mean, eventB.getStartingTime().getEpochSecond(),
                                false, eventB.getDeparture(), eventB.getEventLocation() );
            } catch ( JSONException err ) {
                Logger.getLogger( PathManager.class.getName() ).log( Level.SEVERE, err.getMessage(), err );
            }
            // Considering only paths that allow to attend to the event in time
            for ( Travel travel : privatePaths )
                if ( travelFeasibleInTimeslot( eventA, eventB, travel ) )
                    possiblePaths.add( travel );
        }
    }

    private void publicPathsHandler( List< Travel > possiblePaths, String baseCall, Event eventA, Event eventB,
                                     List< TravelMeanEnum > publicMeans, boolean sameLoc )
            throws GMapsGeneralException {
        GMapsJSONReader reader = new GMapsJSONReader();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        if ( !sameLoc ) { // If departure location is the same of arrival location, the path can be done only
            // with private means.
            ArrayList< Travel > publicPaths = new ArrayList< Travel >();
            try {
                JSONObject publicPathsJSON = HTMLCallAndResponse.performCall(
                        directionsHandler.getCallByTransit( baseCall, publicMeans ) );
                publicPaths = reader.getTravelWithTransitMeans( publicPathsJSON );
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
            for ( Travel travel : publicPaths )
                if ( travelFeasibleInTimeslot( eventA, eventB, travel ) )
                    possiblePaths.add( travel );
        }
    }

    private boolean travelFeasibleInTimeslot( Event eventA, Event eventB, Travel travel ) {
        if ( eventA == null ) {
            return true;
        }
        return ( !eventB.isTravelAtLastChoice() && !travel.getEndingTime().isAfter( eventB.getStartingTime() ) ) ||
                ( eventB.isTravelAtLastChoice() && !travel.getStartingTime().isBefore( eventA.getEndingTime() ) );
    }

    private boolean isBetweenSameLocations( Event event ) {
        return event.getDeparture().getLatitude() == event.getEventLocation().getLatitude() &&
                event.getDeparture().getLongitude() == event.getEventLocation().getLongitude();
    }

    public ArrayList< GenericEvent > swapEvents( Event forcedEvent, ArrayList< TravelMeanEnum > privateMeans,
                                                 ArrayList< TravelMeanEnum > publicMeans ) {
        scheduleManager.setSchedule( forcedEvent.getStartingTime(), ScheduleManager.SECONDS_IN_A_DAY );
        List< GenericEvent > swapOutEvents = new ArrayList< GenericEvent >();
        ArrayList< PathCombination > combs = new ArrayList< PathCombination >();
        firstSwapPhase( forcedEvent, swapOutEvents );
        // Calculating prev/foll path for the forcedEvent.
        boolean complete = false;
        while ( !complete && !scheduleManager.getSchedule().getEvents().isEmpty() ) {
            List< Travel > prev = getPreviousTravels( forcedEvent, privateMeans, publicMeans );
            List< Travel > foll = getFollowingTravels( forcedEvent, privateMeans, publicMeans );
            prev = prev.stream().filter( p -> preferenceManager.checkConstraints( p, forcedEvent.getType() ) )
                    .collect( Collectors.toList() );
            foll = foll.stream().filter( p -> preferenceManager.checkConstraints( p, forcedEvent.getType() ) )
                    .collect( Collectors.toList() );
            // Prev and foll paths are founded. Checking the feasibility with scheduled break events.
            if ( !prev.isEmpty() && ( !foll.isEmpty() || scheduleManager.getPossibleFollowingEvent(
                    forcedEvent.getStartingTime() ) == null ) ) {
                combs = scheduleManager.getFeasiblePathCombinations( forcedEvent, prev, foll );
                if ( combs.isEmpty() ) {
                    BreakEvent toRemove = breakToRemove( forcedEvent );
                    scheduleManager.getSchedule().removeSpecBreak( toRemove );
                    swapOutEvents.add( toRemove );
                } else
                    complete = true;
            }
            // If prev or foll path is not founded, a related-event is removed from the schedule.
            else if ( prev.isEmpty() && scheduleManager.getPossiblePreviousEvent(
                    forcedEvent.getStartingTime() ) != null ) {
                swapOutEvents.add( scheduleManager.getPossiblePreviousEvent( forcedEvent.getStartingTime() ) );
                scheduleManager.getSchedule().removeSpecEvent( scheduleManager.getPossiblePreviousEvent(
                        forcedEvent.getStartingTime() ) );
            } else if ( foll.isEmpty() && scheduleManager.getPossibleFollowingEvent(
                    forcedEvent.getStartingTime() ) != null ) {
                swapOutEvents.add( scheduleManager.getPossibleFollowingEvent( forcedEvent.getStartingTime() ) );
                scheduleManager.getSchedule().removeSpecEvent( scheduleManager.getPossibleFollowingEvent(
                        forcedEvent.getStartingTime() ) );
            }
        }
        PathCombination best = ( complete ) ? preferenceManager.findBestpath( combs, forcedEvent.getType() ) : null;
        return best != null ? conclusionForSwap( best, forcedEvent, swapOutEvents ) : null;
    }

    private ArrayList< GenericEvent > conclusionForSwap( PathCombination best, Event forcedEvent,
                                                         List< GenericEvent > swapOut ) {
        ArrayList< GenericEvent > response = new ArrayList< GenericEvent >();
        // Updating swap out events into DB removing scheduled param and path.
        for ( GenericEvent genEv : swapOut ) {
            genEv.setScheduled( false );
            genEv.removeFeasiblePath();
            genEv.save();
            response.add( genEv );
        }
        // Updating swap in event into DB adding scheduled param and path.
        forcedEvent.setScheduled( true );
        forcedEvent.setFeasiblePath( best.getPrevPath() );
        forcedEvent.save();
        response.add( forcedEvent );
        // Updating following path into DB
        Event following = scheduleManager.getPossibleFollowingEvent( forcedEvent.getStartingTime() );
        if ( following != null ) {
            following.setFeasiblePath( best.getFollPath() );
            following.save();
            response.add( following );
        }
        return response;
    }

    private BreakEvent breakToRemove( Event forcedEvent ) {
        if ( scheduleManager.getSchedule().getBreaks().isEmpty() )
            return null;
        BreakEvent toRemove = scheduleManager.getSchedule().getBreaks().get( 0 );
        for ( int i = 1; i < scheduleManager.getSchedule().getBreaks().size(); i++ )
            if ( scheduleManager.overlappingTime( scheduleManager.getSchedule().getBreaks().get( i ), forcedEvent ) <
                    scheduleManager.overlappingTime( toRemove, forcedEvent ) )
                toRemove = scheduleManager.getSchedule().getBreaks().get( i );
        return toRemove;
    }

    // Removing events that overlap with forcedEvent
    private void firstSwapPhase( Event forcedEvent, List< GenericEvent > swapOutEvents ) {
        for ( Event event : scheduleManager.getSchedule().getEvents() )
            if ( !scheduleManager.areEventsOverlapFree( event, forcedEvent ) )
                swapOutEvents.add( event );
        for ( GenericEvent event : swapOutEvents )
            scheduleManager.getSchedule().removeSpecEvent( ( Event ) event );
    }

    private ArrayList< TravelMeanEnum > privateMeansSameLoc( List< TravelMeanEnum > privateMeans ) {
        ArrayList< TravelMeanEnum > copy = new ArrayList< TravelMeanEnum >();
        for ( TravelMeanEnum mean : privateMeans )
            if ( !mean.getParam().equals( "driving" ) )
                copy.add( mean );
        if ( !copy.contains( TravelMeanEnum.BY_FOOT ) )
            copy.add( TravelMeanEnum.BY_FOOT );
        return copy;
    }

    @Override
    public void setCurrentUser( User currentUser ) {
        this.currentUser = currentUser;
        this.scheduleManager.setCurrentUser( currentUser );
        this.preferenceManager.setCurrentUser( currentUser );
    }

}
