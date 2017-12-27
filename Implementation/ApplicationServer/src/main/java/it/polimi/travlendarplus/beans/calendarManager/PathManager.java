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
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.BadRequestException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsUnavailableException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.LocationNotFoundException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class provide all methods related to handle the path computation
 */
@Stateless
public class PathManager extends UserManager {

    private static final long MARGIN_TIME = 5 * 60;
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
     * Compute the best path relative to a given event calculating the schedule.
     *
     * @param event        event that need a feasible path.
     * @param privateMeans allowed private travel means according to event preferences.
     * @param publicMeans  allowed public travel means according to event preferences.
     * @return the requested path if available by means of calcPathNoSetSchedule, null if it doesn't exists.
     * @throws GMapsGeneralException if the path computation fails cause Google maps services are unavailable.
     */
    public PathCombination calculatePath ( Event event, List < TravelMeanEnum > privateMeans,
                                           List < TravelMeanEnum > publicMeans ) throws GMapsGeneralException {
        scheduleManager.setSchedule( event.getStartingTime(), ScheduleManager.SECONDS_IN_A_DAY );
        return calcPathNoSetSchedule( event, privateMeans, publicMeans );
    }

    /**
     * Compute the best path relative to a given event without calculate the schedule. It is used in swap function and
     * it is called in all the other situations after that the schedule is calculated.
     *
     * @param event        event that need a feasible path.
     * @param privateMeans allowed private travel means according to event preferences.
     * @param publicMeans  allowed public travel means according to event preferences.
     * @return the requested path if available, null otherwise.
     * @throws GMapsGeneralException if the path computation fails cause Google maps services are unavailable.
     */
    private PathCombination calcPathNoSetSchedule ( Event event, List < TravelMeanEnum > privateMeans,
                                                    List < TravelMeanEnum > publicMeans ) throws GMapsGeneralException {
        // Obtaining possible paths that don't overlap with previous and following scheduled events.
        List < Travel > previousPaths = getPreviousTravels( event, privateMeans, publicMeans );
        List < Travel > followingPaths = getFollowingTravels( event, privateMeans, publicMeans );
        // Filtering obtained paths according to constraints defined
        previousPaths = previousPaths.stream().filter( p -> preferenceManager.checkConstraints( p, event.getType() ) )
                .collect( Collectors.toCollection( ArrayList::new ) );
        followingPaths = followingPaths.stream().filter( p -> preferenceManager.checkConstraints( p, event.getType() ) )
                .collect( Collectors.toCollection( ArrayList::new ) );
        // Selecting only combinations of paths that ensure feasibility for each scheduled break event.
        List < PathCombination > possibleCombinations = scheduleManager.getFeasiblePathCombinations( event,
                previousPaths, followingPaths );
        if ( possibleCombinations.isEmpty() ) {
            return null;
        }
        PathCombination bestPath = preferenceManager.findBestPath( possibleCombinations, event.getType() );
        bestPath.fixTimes();
        return bestPath;
    }

    /**
     * Defines the departure location for an event and calculates all possible paths according to the schedule.
     *
     * @param event        the event of which previous travels are calculated.
     * @param privateMeans list of allowed private means.
     * @param publicMeans  list of allowed public means.
     * @return all possible previous paths according to the schedule.
     * @throws GMapsGeneralException if the path computation fails cause Google maps services are unavailable.
     */
    private List < Travel > getPreviousTravels ( Event event, List < TravelMeanEnum > privateMeans,
                                                 List < TravelMeanEnum > publicMeans ) throws GMapsGeneralException {
        List < Travel > possiblePaths = new ArrayList < Travel >();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        Event previous = scheduleManager.getPossiblePreviousEvent( event.getStartingTime() );
        if ( event.isPrevLocChoice() ) {
            event.setDeparture( ( previous != null ) ? previous.getEventLocation() : event.getEventLocation() );
        }
        try {
            boolean prevDateAllowed = previousDateAllowed( previous );
            // Obtaining baseCall string for previous paths, here locations and times are setted.
            String baseCall = directionsHandler.getBaseCallPreviousPath( event, previous, prevDateAllowed );
            boolean sameLoc = isBetweenSameLocations( event );
            // Obtaining possible paths for the specified means.
            possiblePaths = possiblePathsAdder( baseCall, privateMeans, publicMeans, previous, event, sameLoc,
                    prevDateAllowed );
        } catch ( GMapsGeneralException e ) {
            Logger.getLogger( PathManager.class.getName() ).log( Level.SEVERE, e.getMessage(), e );
            throw e;
        }
        return possiblePaths;
    }

    /**
     * Checks if an event is the last of the schedule and calculates all possible paths according to the schedule.
     *
     * @param event        the event of which following travels are calculated.
     * @param privateMeans list of allowed private means.
     * @param publicMeans  list of allowed public means.
     * @return all possible following paths according to the schedule, null if it oesn't exist any following event.
     * @throws GMapsGeneralException if the path computation fails cause Google maps services are unavailable.
     */
    private List < Travel > getFollowingTravels ( Event event, List < TravelMeanEnum > privateMeans,
                                                  List < TravelMeanEnum > publicMeans ) throws GMapsGeneralException {
        List < Travel > possiblePaths = new ArrayList < Travel >();
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        Event following = scheduleManager.getPossibleFollowingEvent( event.getStartingTime() );
        if ( following == null )
        // The event would be the last of that day. An empty ArrayList is returned.
        {
            return possiblePaths;
        }
        try {
            if ( following.isPrevLocChoice() ) {
                following.setDeparture( event.getEventLocation() );
            }
            boolean prevDateAllowed = previousDateAllowed( event );
            // Obtaining baseCall string for previous paths, here locations and times are setted.
            String baseCall = directionsHandler.getBaseCallFollowingPath( event, following, prevDateAllowed );
            boolean sameLoc = isBetweenSameLocations( following );
            // Obtaining possible paths for the specified means.
            possiblePaths = possiblePathsAdder( baseCall, privateMeans, publicMeans, event, following, sameLoc,
                    prevDateAllowed );
        } catch ( GMapsGeneralException e ) {
            Logger.getLogger( PathManager.class.getName() ).log( Level.SEVERE, e.getMessage(), e );
            throw e;
        }
        return possiblePaths;
    }


    /**
     * Calculates private and eventually public travels according to specified means, events, times and locations.
     *
     * @param baseCall        url to use containing info on locations and time of the path.
     * @param privateMeans    list of allowed private means.
     * @param publicMeans     list of allowed public means.
     * @param eventA          event before the path.
     * @param eventB          event after the path.
     * @param sameLoc         boolean that indicates if the path starts and ends in the same location.
     * @param prevDateAllowed boolean that indicates if the date related to previous event is not in the past, in this
     *                        case it can be used to calculate the path.
     * @return all the possible paths for a given travel, according to the user's schedule.
     * @throws GMapsGeneralException if the path computation fails cause Google maps services are unavailable.
     */
    private List < Travel > possiblePathsAdder ( String baseCall, List < TravelMeanEnum > privateMeans,
                                                 List < TravelMeanEnum > publicMeans, Event eventA, Event eventB,
                                                 boolean sameLoc, boolean prevDateAllowed )
            throws GMapsGeneralException {
        ArrayList < Travel > possiblePaths = new ArrayList < Travel >();
        privatePathsHandler( possiblePaths, baseCall, eventA, eventB, privateMeans, sameLoc, prevDateAllowed );
        if ( !publicMeans.isEmpty() ) {
            publicPathsHandler( possiblePaths, baseCall, eventA, eventB, publicMeans, sameLoc );
        }
        return possiblePaths;
    }

    /**
     * Calculates possible paths whose travel means can be: car, bike or walking.
     *
     * @param possiblePaths   list of possible paths where the result found in the function must be added.
     * @param baseCall        base url containing info on locations and time that must be enriched before perform the
     *                        call.
     * @param eventA          event before the path.
     * @param eventB          event after the path.
     * @param privateMeans    list of private means used in the calculated paths.
     * @param sameLoc         boolean that indicates if the path starts and ends in the same location.
     * @param prevDateAllowed boolean that indicates if the date related to previous event is not in the past, in this
     *                        case it can be used to calculate the path.
     * @throws GMapsUnavailableException if the path computation fails cause Google maps services are unavailable.
     * @throws BadRequestException       if the path computation fails cause a wrong request.
     * @throws LocationNotFoundException if the path computation fails cause the location specified is not found.
     */
    private void privatePathsHandler ( List < Travel > possiblePaths, String baseCall, Event eventA, Event eventB,
                                       List < TravelMeanEnum > privateMeans, boolean sameLoc, boolean prevDateAllowed )
            throws GMapsUnavailableException, BadRequestException, LocationNotFoundException {
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        if ( sameLoc ) {
            privateMeans = privateMeansSameLoc( privateMeans );
        }
        avoidLongWalkingCalculation( privateMeans, eventB );
        for ( TravelMeanEnum mean : privateMeans ) {
            ArrayList < Travel > privatePaths = new ArrayList < Travel >();
            try {
                GMapsJSONReader reader = new GMapsJSONReader();
                JSONObject privatePathJSON = HTMLCallAndResponse.performCall(
                        directionsHandler.getCallWithNoTransit( baseCall, mean ) );
                /*Two cases:
                1) eventA is the previous and eventB the main event -> it is managed the case in which eventA is NULL.
                2) eventA is the main event and eventB is the following event -> eventB is not NULL
                 because this case is managed above.*/
                privatePaths = ( eventA != null && !eventB.isTravelAtLastChoice() && prevDateAllowed )
                        ? reader.getTravelNoTransitMeans( privatePathJSON, mean, eventA.getEndingTime()
                        .getEpochSecond(), true, eventB.getDeparture(), eventB.getEventLocation() )
                        // It is the case when the possible new event would be the first in the schedule.
                        : reader.getTravelNoTransitMeans( privatePathJSON, mean, eventB.getStartingTime()
                        .getEpochSecond(), false, eventB.getDeparture(), eventB.getEventLocation() );
            } catch ( JSONException err ) {
                Logger.getLogger( PathManager.class.getName() ).log( Level.SEVERE, err.getMessage(), err );
            }
            // Considering only paths that allow to attend to the event in time
            for ( Travel travel : privatePaths ) {
                if ( travelFeasibleInTimeslot( eventA, eventB, travel ) ) {
                    possiblePaths.add( travel );
                }
            }
        }
    }

    /**
     * Calculates possible paths whose travel means can be: tram, train, bus or subway.
     *
     * @param possiblePaths list of possible paths where the result found in the function must be added.
     * @param baseCall      base url containing info on locations and time that must be enriched before perform the
     *                      call.
     * @param eventA        event before the path.
     * @param eventB        event after the path.
     * @param publicMeans   list of public means used in the calculated paths.
     * @param sameLoc       boolean that indicates if the path starts and ends in the same location.
     * @throws GMapsUnavailableException if the path computation fails cause Google maps services are unavailable.
     * @throws BadRequestException       if the path computation fails cause a wrong request.
     * @throws LocationNotFoundException if the path computation fails cause the location specified is not found.
     */
    private void publicPathsHandler ( List < Travel > possiblePaths, String baseCall, Event eventA, Event eventB,
                                      List < TravelMeanEnum > publicMeans, boolean sameLoc )
            throws GMapsGeneralException {
        GMapsDirectionsHandler directionsHandler = new GMapsDirectionsHandler();
        if ( !sameLoc ) { // If departure location is the same of arrival location, the path can be done only
            // with private means.
            ArrayList < Travel > publicPaths = new ArrayList < Travel >();
            try {
                GMapsJSONReader reader = new GMapsJSONReader();
                JSONObject publicPathsJSON = HTMLCallAndResponse.performCall(
                        directionsHandler.getCallByTransit( baseCall, publicMeans ) );
                publicPaths = reader.getTravelWithTransitMeans( publicPathsJSON );
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
            for ( Travel travel : publicPaths ) {
                if ( travelFeasibleInTimeslot( eventA, eventB, travel ) ) {
                    possiblePaths.add( travel );
                }
            }
        }
    }

    /**
     * States if the ending time of the event passed can be used as reference time to calculate paths.
     *
     * @param eventA event before the path.
     * @return true if the ending time of the event is not in the past, false otherwise.
     */
    private boolean previousDateAllowed ( Event eventA ) {
        if ( eventA == null ) {
            return false;
        }
        return eventA.getEndingTime().isAfter( Instant.ofEpochSecond( Instant.now().getEpochSecond() + MARGIN_TIME ) );
    }

    /**
     * States if travels are feasible in the time between two events.
     *
     * @param eventA the event before the travel.
     * @param eventB the event after the travel.
     * @param travel the travel to control.
     * @return true if the travel can be done without overlapping in the schedule.
     */
    private boolean travelFeasibleInTimeslot ( Event eventA, Event eventB, Travel travel ) {
        if ( eventA == null ) {
            return true;
        }
        return ( !eventB.isTravelAtLastChoice() && !travel.getEndingTime().isAfter( eventB.getStartingTime() ) ) ||
                ( eventB.isTravelAtLastChoice() && !travel.getStartingTime().isBefore( eventA.getEndingTime() ) );
    }

    /**
     * States if a path must be calculated between the same locations
     *
     * @param event the event involved into the calculation of path.
     * @return true if departure locaction and event location are almost the same, false otherwise.
     */
    private boolean isBetweenSameLocations ( Event event ) {
        return Math.abs( event.getDeparture().getLatitude() - event.getEventLocation().getLatitude() ) < 0.001 &&
                Math.abs( event.getDeparture().getLongitude() - event.getEventLocation().getLongitude() ) < 0.001;
    }

    /**
     * Forces an overlapping event into the schedule and handles the swap out operations of any scheduled event that is
     * in conflict with the forced one.
     *
     * @param forcedEvent  event that must be inserted into the schedule.
     * @param privateMeans list of allowed private means for the forcedEvent.
     * @param publicMeans  list of allowed public means for the forcedEvent.
     * @return a list of modified events or a new ArrayList if the swap operation is not feasible.
     * @throws GMapsGeneralException if the path computation fails cause Google maps services are unavailable.
     */
    public List < GenericEvent > swapEvents ( Event forcedEvent, List < TravelMeanEnum > privateMeans,
                                              List < TravelMeanEnum > publicMeans ) throws GMapsGeneralException {
        scheduleManager.setSchedule( forcedEvent.getStartingTime(), ScheduleManager.SECONDS_IN_A_DAY );
        List < GenericEvent > swapOutEvents = new ArrayList < GenericEvent >();
        List < PathCombination > combs = new ArrayList < PathCombination >();
        firstSwapPhase( forcedEvent, swapOutEvents );
        // Calculating prev/foll path for the forcedEvent.
        boolean complete = false;
        while ( !complete && ( !scheduleManager.getSchedule().getEvents().isEmpty() ||
                !scheduleManager.getSchedule().getBreaks().isEmpty() ) ) {
            List < Travel > prev = getPreviousTravels( forcedEvent, privateMeans, publicMeans );
            List < Travel > foll = getFollowingTravels( forcedEvent, privateMeans, publicMeans );
            prev = prev.stream().filter( p -> preferenceManager.checkConstraints( p, forcedEvent.getType() ) )
                    .collect( Collectors.toCollection( ArrayList::new ) );
            foll = foll.stream().filter( p -> preferenceManager.checkConstraints( p, forcedEvent.getType() ) )
                    .collect( Collectors.toCollection( ArrayList::new ) );
            // Prev and foll paths are founded. Checking the feasibility with scheduled break events.
            if ( !prev.isEmpty() && ( !foll.isEmpty() || scheduleManager.getPossibleFollowingEvent(
                    forcedEvent.getStartingTime() ) == null ) ) {
                combs = scheduleManager.getFeasiblePathCombinations( forcedEvent, prev, foll );
                if ( combs.isEmpty() ) {
                    BreakEvent toRemove = breakToRemove( forcedEvent );
                    scheduleManager.getSchedule().removeSpecBreak( toRemove );
                    swapOutEvents.add( toRemove );
                } else {
                    complete = true;
                }
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
        PathCombination best = ( complete ) ? preferenceManager.findBestPath( combs, forcedEvent.getType() ) :
                calcPathNoSetSchedule( forcedEvent, privateMeans, publicMeans );
        if ( best != null ) {
            best.fixTimes();
            return conclusionForSwap( best, forcedEvent, swapOutEvents );
        }
        return new ArrayList <>();
    }

    /**
     * Performs saving operations for the events modified after the swap operations.
     *
     * @param best        path combination calculated for the event swapped into the schedule.
     * @param forcedEvent overlapping event forced into the schedule
     * @param swapOut     events that must be swapped out due to the swapped in event.
     * @return a list of modified events that can be used by the client application.
     */
    private List < GenericEvent > conclusionForSwap ( PathCombination best, Event forcedEvent,
                                                      List < GenericEvent > swapOut ) {
        ArrayList < GenericEvent > response = new ArrayList < GenericEvent >();
        // Updating swap out events into DB removing scheduled param and path.
        for ( GenericEvent genEv : swapOut ) {
            addRemovingEventToResponse( response, genEv );
        }
        // Updating swap in event into DB adding scheduled param and path.
        addEventWithNewPathToResponse( response, forcedEvent, best.getPrevPath() );
        // Updating following path into DB
        Event following = scheduleManager.getPossibleFollowingEvent( forcedEvent.getStartingTime() );
        if ( following != null ) {
            addEventWithNewPathToResponse( response, following, best.getFollPath() );
        }
        scheduleManager.saveForSwap( response );
        return response;
    }

    /**
     * Determines which break event must be swapped out, from the less overlapping one.
     *
     * @param forcedEvent event to swap into the schedule
     * @return break event to remove in order to ensure the swap in of an event, null if there is no break event
     * scheduled.
     */
    private BreakEvent breakToRemove ( Event forcedEvent ) {
        if ( scheduleManager.getSchedule().getBreaks().isEmpty() ) {
            return null;
        }
        BreakEvent toRemove = scheduleManager.getSchedule().getBreaks().get( 0 );
        for ( int i = 1; i < scheduleManager.getSchedule().getBreaks().size(); i++ ) {
            if ( scheduleManager.overlappingTime( scheduleManager.getSchedule().getBreaks().get( i ), forcedEvent ) <
                    scheduleManager.overlappingTime( toRemove, forcedEvent ) ) {
                toRemove = scheduleManager.getSchedule().getBreaks().get( i );
            }
        }
        return toRemove;
    }

    /**
     * Removes from the schedule all the scheduled events that overlap with the forced event, without considering
     * related-paths.
     *
     * @param forcedEvent   event to swap in.
     * @param swapOutEvents a list where all swap out-events must be added.
     */
    private void firstSwapPhase ( Event forcedEvent, List < GenericEvent > swapOutEvents ) {
        for ( Event event : scheduleManager.getSchedule().getEvents() ) {
            if ( !scheduleManager.areEventsOverlapFree( forcedEvent, event ) ) {
                swapOutEvents.add( event );
            }
        }
        for ( GenericEvent event : swapOutEvents ) {
            scheduleManager.getSchedule().removeSpecEvent( ( Event ) event );
        }
    }

    /**
     * Controls the path calculation when starting and ending locations are the same: remove car  if it is in the list
     * and add by_foot if it is not in the list.
     *
     * @param privateMeans list of allowed private travel means.
     * @return the list with by_foot and without car.
     */
    private List < TravelMeanEnum > privateMeansSameLoc ( List < TravelMeanEnum > privateMeans ) {
        ArrayList < TravelMeanEnum > copy = new ArrayList < TravelMeanEnum >();
        for ( TravelMeanEnum mean : privateMeans ) {
            if ( !mean.getParam().equals( "driving" ) ) {
                copy.add( mean );
            }
        }
        if ( !copy.contains( TravelMeanEnum.BY_FOOT ) ) {
            copy.add( TravelMeanEnum.BY_FOOT );
        }
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
     * Force a genericEvent into the schedule, removing all the events that overlaps with it
     *
     * @param eventId identifier of the event to be forced into the schedule
     * @return a list of generic events, modified during the swap
     * @throws EntityNotFoundException   if the event to be swapped does not exist
     * @throws AlreadyScheduledException if the event is already in the schedule,
     *                                   and so it can't be forced into it
     */
    public List < GenericEvent > swapGenericEvent ( long eventId )
            throws EntityNotFoundException, AlreadyScheduledException, GMapsGeneralException {

        ArrayList < GenericEvent > genericEvents = new ArrayList <>( currentUser.getEvents() );
        genericEvents.addAll( currentUser.getBreaks() );
        //NB the swap in the first release is available only for events and not break events!
        GenericEvent genericEvent = EventManager.extractEvent( genericEvents, eventId );
        if ( genericEvent.isScheduled() ) {
            throw new AlreadyScheduledException();
        }

        return genericEvent.swap( this );
    }

    /**
     * Force a event into the schedule, removing all the events that overlaps with it
     *
     * @param forcedEvent event to be forced into the schedule
     * @return a list of modified ( due to the swap ) events
     * @throws GMapsGeneralException if Google Maps services are unavailable
     */
    public List < GenericEvent > swapEvent ( Event forcedEvent ) throws GMapsGeneralException {
        return swapEvents( forcedEvent, preferenceManager.getAllowedMeans( forcedEvent, privateList ),
                preferenceManager.getAllowedMeans( forcedEvent, publicList ) );
    }

    public List < GenericEvent > swapBreakEvent ( BreakEvent forcedBreak ) throws GMapsGeneralException {
        List < GenericEvent > response = new ArrayList < GenericEvent >();
        scheduleManager.setSchedule( forcedBreak.getStartingTime(), ScheduleManager.SECONDS_IN_A_DAY );
        swapOutOverlappingBreaks( response, forcedBreak );
        List < Event > involved = scheduleManager.getEventsIntoIntervalWithPathRegard( scheduleManager.getSchedule().
                getEvents(), forcedBreak );
        while ( !involved.isEmpty() && !forcedBreak.isMinimumEnsuredWithPathRegard( involved ) ) {
            Event mostOverlapping = eventToRemove( involved, forcedBreak );
            Event following = scheduleManager.getPossibleFollowingEvent( mostOverlapping.getStartingTime() );
            scheduleManager.getSchedule().removeSpecEvent( mostOverlapping );
            addRemovingEventToResponse( response, mostOverlapping );
            adjustFollowing( response, following );
            involved = scheduleManager.getEventsIntoIntervalWithPathRegard( scheduleManager.getSchedule().
                    getEvents(), forcedBreak );
        }
        forcedBreak.setScheduled( true );
        response.add( forcedBreak );
        scheduleManager.saveForSwap( response );
        return response;
    }

    private void swapOutOverlappingBreaks ( List < GenericEvent > response, BreakEvent forcedBreak ) {
        // Removing all break events overlapping with the forced one
        List < BreakEvent > freeBreaks = scheduleManager.getSchedule().getBreaks().stream().filter( br ->
                scheduleManager.areEventsOverlapFree( forcedBreak, br ) )
                .collect( Collectors.toCollection( ArrayList::new ) );
        // Adding to response the removed breaks
        List < BreakEvent > swapOutBreaks = scheduleManager.getSchedule().getBreaks().stream().filter( br ->
                !scheduleManager.areEventsOverlapFree( forcedBreak, br ) )
                .collect( Collectors.toCollection( ArrayList::new ) );
        for ( BreakEvent br : swapOutBreaks ) {
            addRemovingEventToResponse( response, br );
        }
        scheduleManager.getSchedule().setBreaks( freeBreaks );
    }

    private Event eventToRemove ( List < Event > involved, BreakEvent forcedBreak ) {
        Event toRemove = involved.get( 0 );
        for ( int i = 1; i < involved.size(); i++ ) {
            if ( scheduleManager.overlappingEventWithBreak( involved.get( i ), forcedBreak ) >
                    scheduleManager.overlappingEventWithBreak( toRemove, forcedBreak ) ) {
                toRemove = involved.get( i );
            }
        }
        return toRemove;
    }

    private void adjustFollowing ( List < GenericEvent > response, Event following ) throws GMapsGeneralException {
        if ( following == null ) {
            return;
        }
        List < TravelMeanEnum > pubEnum = preferenceManager.getAllowedMeans( following, publicList );
        List < TravelMeanEnum > priEnum = preferenceManager.getAllowedMeans( following, privateList );
        // Getting possible paths before the event
        List < Travel > paths = getPreviousTravels( following, priEnum, pubEnum );
        paths = paths.stream().filter( p -> preferenceManager.checkConstraints( p, following.getType() ) )
                .collect( Collectors.toCollection( ArrayList::new ) );
        // Use the path in the schedule that follow the event to simulate the path calculation
        ArrayList < Travel > simFoll = new ArrayList < Travel >();
        simFoll.add( ( scheduleManager.getPossibleFollowingEvent( following.getStartingTime() ) != null )
                ? scheduleManager.getPossibleFollowingEvent( following.getStartingTime() ).getFeasiblePath()
                : null );
        List < PathCombination > combs = scheduleManager.getFeasiblePathCombinations( following, paths, simFoll );
        // A feasible path is found, adjusting the schedule with the new path
        if ( !combs.isEmpty() ) {
            PathCombination pathComb = preferenceManager.findBestPath( combs, following.getType() );
            pathComb.fixTimes();
            scheduleManager.getSchedule().changeEventPath( following.getId(), pathComb.getPrevPath() );
            addEventWithNewPathToResponse( response, following, pathComb.getPrevPath() );
        } else { // No feasible path according to user preferences: removing the event and trying with the next one
            Event nextFoll = scheduleManager.getPossibleFollowingEvent( following.getStartingTime() );
            scheduleManager.getSchedule().removeSpecEvent( following );
            addRemovingEventToResponse( response, following );
            adjustFollowing( response, nextFoll );
        }
    }

    private void addRemovingEventToResponse ( List < GenericEvent > response, GenericEvent event ) {
        event.setScheduled( false );
        event.removeFeasiblePath();
        scheduleManager.saveEvent( event );
        response.add( event );
    }

    private void addEventWithNewPathToResponse ( List < GenericEvent > response, Event event, Travel path ) {
        event.setScheduled( true );
        scheduleManager.savePath( path );
        event.setFeasiblePath( path );
        scheduleManager.saveEvent( event );
        response.add( event );
    }

    private void avoidLongWalkingCalculation ( List < TravelMeanEnum > privateMeans, Event event ) {
        float pk = ( float ) ( 180.f / Math.PI );

        double a1 = event.getDeparture().getLatitude() / pk;
        double a2 = event.getDeparture().getLongitude() / pk;
        double b1 = event.getEventLocation().getLatitude() / pk;
        double b2 = event.getEventLocation().getLongitude() / pk;

        double t1 = Math.cos( a1 ) * Math.cos( a2 ) * Math.cos( b1 ) * Math.cos( b2 );
        double t2 = Math.cos( a1 ) * Math.sin( a2 ) * Math.cos( b1 ) * Math.sin( b2 );
        double t3 = Math.sin( a1 ) * Math.sin( b1 );
        double tt = Math.acos( t1 + t2 + t3 );

        if ( 6366 * tt > 30 ) {
            privateMeans.removeIf( e -> e.getParam().equals( "walking" ) );
        }
    }

}
