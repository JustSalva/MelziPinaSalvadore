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
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.AlreadyScheduledException;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.NotScheduledException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;

/**
 * This class provide all methods related to handle the path computation
 */
@Stateless
public class PathManager extends UserManager {

    private final static float MAX_LENGTH = 2.5f;

    protected static TravelMeanEnum[] privateList = { TravelMeanEnum.CAR, TravelMeanEnum.BIKE, TravelMeanEnum.BY_FOOT };
    protected static TravelMeanEnum[] publicList = { TravelMeanEnum.TRAIN, TravelMeanEnum.BUS, TravelMeanEnum.TRAM,
            TravelMeanEnum.SUBWAY };
    @EJB
    private ScheduleManager scheduleManager;
    @EJB
    private PreferenceManager preferenceManager;

    /**
     * Initialize all the nested java beans with the current user
     */
    @PostConstruct
    public void postConstruct () {
        scheduleManager.setCurrentUser( currentUser );
        preferenceManager.setCurrentUser( currentUser );
    }

    /**
     * Compute a the best path relative to a given event
     *
     * @param event event that need a feasible path
     * @param privateMeans allowed private travel means according to event preferences
     * @param publicMeans allowed public travel means according to event preferences
     * @return the requested path if available, null otherwise
     */
    public PathCombination calculatePath ( Event event, List < TravelMeanEnum > privateMeans,
                                           List < TravelMeanEnum > publicMeans ) {
        scheduleManager.setSchedule( event.getStartingTime(), ScheduleManager.SECONDS_IN_A_DAY );
        // Obtaining possible paths that don't overlap with previous and following scheduled events.
        List < Travel > previousPaths = getPreviousTravels( event, privateMeans, publicMeans );
        List < Travel > followingPaths = getFollowingTravels( event, privateMeans, publicMeans );
        // Filtering obtained paths according to constraints defined
        previousPaths = previousPaths.stream().filter( p -> preferenceManager.checkConstraints( p, event.getType() ) )
                .collect( toList() );
        followingPaths = followingPaths.stream().filter( p -> preferenceManager.checkConstraints( p, event.getType() ) )
                .collect( toList() );
        // Selecting only combinations of paths that ensure feasibility for each scheduled break event.
        List < PathCombination > possibleCombinations = scheduleManager.getFeasiblePathCombinations( event,
                previousPaths, followingPaths );
        return ( !possibleCombinations.isEmpty() ) ? preferenceManager.findBestPath(
                possibleCombinations, event.getType() )
                : null;
    }

    private List < Travel > getPreviousTravels ( Event event, List < TravelMeanEnum > privateMeans,
                                                      List < TravelMeanEnum > publicMeans ) {
        List < Travel > possiblePaths = new ArrayList < Travel >();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        Event previous = scheduleManager.getPossiblePreviousEvent( event.getStartingTime() );
        if ( event.isPrevLocChoice() && previous != null ) {
            event.setDeparture( previous.getEventLocation() );
        } else if ( event.isPrevLocChoice() && previous == null ) {
            event.setDeparture( event.getEventLocation() );
        }
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

    private List < Travel > getFollowingTravels ( Event event, List < TravelMeanEnum > privateMeans,
                                                       List < TravelMeanEnum > publicMeans ) {
        List < Travel > possiblePaths = new ArrayList < Travel >();
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

    private List < Travel > possiblePathsAdder ( String baseCall, List < TravelMeanEnum > privateMeans,
                                                      List < TravelMeanEnum > publicMeans, Event eventA, Event eventB,
                                                      boolean sameLoc )
            throws GMapsGeneralException {
        ArrayList < Travel > possiblePaths = new ArrayList < Travel >();
        privatePathsHandler( possiblePaths, baseCall, eventA, eventB, privateMeans, sameLoc );
        if ( !publicMeans.isEmpty() ) {
            publicPathsHandler( possiblePaths, baseCall, eventA, eventB, publicMeans, sameLoc );
        }
        return possiblePaths;
    }

    private void privatePathsHandler ( List < Travel > possiblePaths, String baseCall, Event eventA, Event eventB,
                                       List < TravelMeanEnum > privateMeans, boolean sameLoc )
            throws GMapsGeneralException {
        GMapsJSONReader reader = new GMapsJSONReader();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        if ( sameLoc )
            privateMeans = privateMeansSameLoc( privateMeans );
        for ( TravelMeanEnum mean : privateMeans ) {
            ArrayList < Travel > privatePaths = new ArrayList < Travel >();
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
        // Deleting long walking path...if there are alternatives
        removeLongWalkingPath( possiblePaths );
    }

    private void removeLongWalkingPath ( List < Travel > possiblePaths ) {
        // Checking if long walking paths are not the only feasible...
        if ( possiblePaths.stream().filter( p -> !p.getMiniTravels().get( 0 ).getMeanUsed().getType().getParam().
                equals( "walking" ) || p.getMiniTravels().get( 0 ).getLength() < MAX_LENGTH ).collect( toList() ).size() > 0 )
            // In the case they are not, removing long walking paths
            possiblePaths = possiblePaths.stream().filter( p -> !p.getMiniTravels().get( 0 ).getMeanUsed().getType().getParam().
                    equals( "walking" ) || p.getMiniTravels().get( 0 ).getLength() < MAX_LENGTH ).collect( toList() );
    }

    private void publicPathsHandler ( List < Travel > possiblePaths, String baseCall, Event eventA, Event eventB,
                                      List < TravelMeanEnum > publicMeans, boolean sameLoc )
            throws GMapsGeneralException {
        GMapsJSONReader reader = new GMapsJSONReader();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        if ( !sameLoc ) { // If departure location is the same of arrival location, the path can be done only
            // with private means.
            ArrayList < Travel > publicPaths = new ArrayList < Travel >();
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

    private boolean travelFeasibleInTimeslot ( Event eventA, Event eventB, Travel travel ) {
        if ( eventA == null ) {
            return true;
        }
        return ( !eventB.isTravelAtLastChoice() && !travel.getEndingTime().isAfter( eventB.getStartingTime() ) ) ||
                ( eventB.isTravelAtLastChoice() && !travel.getStartingTime().isBefore( eventA.getEndingTime() ) );
    }

    private boolean isBetweenSameLocations ( Event event ) {
        return event.getDeparture().getLatitude() == event.getEventLocation().getLatitude() &&
                event.getDeparture().getLongitude() == event.getEventLocation().getLongitude();
    }

    public List < GenericEvent > swapEvents ( Event forcedEvent, List < TravelMeanEnum > privateMeans,
                                                   List < TravelMeanEnum > publicMeans ) {
        scheduleManager.setSchedule( forcedEvent.getStartingTime(), ScheduleManager.SECONDS_IN_A_DAY );
        List < GenericEvent > swapOutEvents = new ArrayList < GenericEvent >();
        List < PathCombination > combs = new ArrayList < PathCombination >();
        firstSwapPhase( forcedEvent, swapOutEvents );
        // Calculating prev/foll path for the forcedEvent.
        boolean complete = false;
        while ( !complete && !scheduleManager.getSchedule().getEvents().isEmpty() ) {
            List < Travel > prev = getPreviousTravels( forcedEvent, privateMeans, publicMeans );
            List < Travel > foll = getFollowingTravels( forcedEvent, privateMeans, publicMeans );
            prev = prev.stream().filter( p -> preferenceManager.checkConstraints( p, forcedEvent.getType() ) )
                    .collect( toList() );
            foll = foll.stream().filter( p -> preferenceManager.checkConstraints( p, forcedEvent.getType() ) )
                    .collect( toList() );
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
        PathCombination best = ( complete ) ? preferenceManager.findBestPath( combs, forcedEvent.getType() ) : null;
        return best != null ? conclusionForSwap( best, forcedEvent, swapOutEvents ) : new ArrayList <>();
    }

    private List < GenericEvent > conclusionForSwap ( PathCombination best, Event forcedEvent,
                                                           List < GenericEvent > swapOut ) {
        ArrayList < GenericEvent > response = new ArrayList < GenericEvent >();
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

    private BreakEvent breakToRemove ( Event forcedEvent ) {
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
    private void firstSwapPhase ( Event forcedEvent, List < GenericEvent > swapOutEvents ) {
        for ( Event event : scheduleManager.getSchedule().getEvents() )
            if ( !scheduleManager.areEventsOverlapFree( event, forcedEvent ) )
                swapOutEvents.add( event );
        for ( GenericEvent event : swapOutEvents )
            scheduleManager.getSchedule().removeSpecEvent( ( Event ) event );
    }

    private List < TravelMeanEnum > privateMeansSameLoc ( List < TravelMeanEnum > privateMeans ) {
        ArrayList < TravelMeanEnum > copy = new ArrayList < TravelMeanEnum >();
        for ( TravelMeanEnum mean : privateMeans )
            if ( !mean.getParam().equals( "driving" ) )
                copy.add( mean );
        if ( !copy.contains( TravelMeanEnum.BY_FOOT ) )
            copy.add( TravelMeanEnum.BY_FOOT );
        return copy;
    }

    public Travel getBestPathInfo ( long pathId ) throws EntityNotFoundException, NotScheduledException {
        Event requestedEvent = currentUser.getEvents()
                .stream().filter( event -> event.getId() == pathId )
                .findFirst().orElse( null );

        if ( requestedEvent == null ) {
            throw new EntityNotFoundException();
        }
        if ( !requestedEvent.isScheduled() ) {
            throw new NotScheduledException();
        }
        return requestedEvent.getFeasiblePath();
    }

    @Override
    public void setCurrentUser ( User currentUser ) {
        this.currentUser = currentUser;
        this.scheduleManager.setCurrentUser( currentUser );
        this.preferenceManager.setCurrentUser( currentUser );
    }

    /**
     * Force an event into the schedule, removing all the events that overlaps with it
     *
     * @param eventId identifier of the event to be forced into the schedule
     * @return a list of generic events, modified during the swap
     * @throws EntityNotFoundException if the event to be swapped does not exist
     * @throws AlreadyScheduledException if the event is already in the schedule,
     * and so it can't be forced into it
     */
    public List < GenericEvent > swapEvents ( long eventId )
            throws EntityNotFoundException, AlreadyScheduledException {

        ArrayList < GenericEvent > genericEvents = new ArrayList <>( currentUser.getEvents() );
        //NB the swap in the first release is available only for events and not break events!
        Event event = ( Event ) EventManager.extractEvent( genericEvents, eventId );
        if ( event.isScheduled() ) {
            throw new AlreadyScheduledException();
        }
        return swapEvents( event, preferenceManager.getAllowedMeans( event, privateList ),
                preferenceManager.getAllowedMeans( event, publicList ) );
    }

}
