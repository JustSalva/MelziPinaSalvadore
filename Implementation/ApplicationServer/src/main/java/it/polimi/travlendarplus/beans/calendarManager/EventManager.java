package it.polimi.travlendarplus.beans.calendarManager;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages.*;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.LocationId;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.calendar.Period;
import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * This class provide all methods related to handle the users events
 */
@Stateless
public class EventManager extends UserManager {

    private static TravelMeanEnum[] privateList = { TravelMeanEnum.CAR, TravelMeanEnum.BIKE, TravelMeanEnum.BY_FOOT };
    private static TravelMeanEnum[] publicList = { TravelMeanEnum.TRAIN, TravelMeanEnum.BUS, TravelMeanEnum.TRAM,
            TravelMeanEnum.SUBWAY };

    @EJB
    private PreferenceManager preferenceManager;
    @EJB
    private ScheduleManager scheduleManager;
    @EJB
    private PathManager pathManager;

    @EJB
    private PeriodicEventsExecutor periodicEventsExecutor;

    /**
     * Initialize all the nested java beans with the current user
     */
    @PostConstruct
    public void postConstruct () {
        preferenceManager.setCurrentUser( currentUser );
        scheduleManager.setCurrentUser( currentUser );
        pathManager.setCurrentUser( currentUser );
    }

    /**
     * Allows to retrieve an event class
     *
     * @param id identifier of the event
     * @return the requested event
     * @throws EntityNotFoundException if the event does not exist
     */
    public Event getEventInformation ( long id ) throws EntityNotFoundException {
        List < GenericEvent > eventList = new ArrayList <>( currentUser.getEvents() );
        return ( Event ) findEvent( eventList, id );
    }

    /**
     * Allows to retrieve a break event class
     *
     * @param id identifier of the break event
     * @return the requested break event
     * @throws EntityNotFoundException if the break event does not exist
     */
    public BreakEvent getBreakEventInformation ( long id ) throws EntityNotFoundException {
        List < GenericEvent > eventList = new ArrayList <>( currentUser.getBreaks() );
        return ( BreakEvent ) findEvent( eventList, id );
    }

    /**
     * Support method that allows to retrieve a generic event from a list
     *
     * @param eventList list of event to be scanned
     * @param id        identifier of the event to be found
     * @return the requested event
     * @throws EntityNotFoundException if the break event does not exist
     */
    private GenericEvent findEvent ( List < GenericEvent > eventList, long id ) throws EntityNotFoundException {
        GenericEvent requestedEvent = eventList.stream()
                .filter( event -> event.getId() == id ).findFirst().orElse( null );
        if ( requestedEvent == null )
            throw new EntityNotFoundException();
        return requestedEvent;
    }

    /**
     * Allows to retrieve all events modified after a certain timestamp
     *
     * @param timestampLocal timestamp of the last requested change
     * @return all the changed events
     */
    public List < GenericEvent > getEventsUpdated ( Instant timestampLocal ) {
        List < GenericEvent > updatedEvents = getEvents();

        updatedEvents = updatedEvents.stream()
                .filter( event -> event.getLastUpdate().getTimestamp().isAfter( timestampLocal ) )
                .collect( Collectors.toCollection( ArrayList::new ) );
        return updatedEvents;
    }

    /**
     * @return all the user's event
     */
    public List < GenericEvent > getEvents () {
        List < GenericEvent > events = new ArrayList <>( currentUser.getEvents() );
        events.addAll( currentUser.getBreaks() );
        return events;
    }

    /**
     * Adds an event into the user's calendar
     *
     * @param eventMessage representation of the event to be added
     * @return the added event, and the modified ones ( path can change after an event insertion)
     * @throws InvalidFieldException if some fields of the message are invalid
     */
    public List < Event > addEvent ( AddEventMessage eventMessage ) throws InvalidFieldException {
        checkEventFields( eventMessage );
        //Create event, initially is not scheduled and non periodic
        Event event = createEvent( eventMessage );
        Event following = addEventAndModifyFollowingEvent( event );
        List < Event > responseList = new ArrayList <>();
        if ( following != null ) {
            following.save();
            responseList.add( following );
        }
        event.setUser( currentUser );
        currentUser.addEvent( event );
        event.save();
        currentUser.save();
        responseList.add( event );
        startEventPropagatorThread( event );
        return responseList;
    }

    /**
     * Provide the event with a feasible path, if it exist, modify also the following event path if needed
     * This method is used in a visitor pattern in order to add the events into the user's calendar
     *
     * @param event event to be added
     * @return the following event, if modified, null otherwise
     */
    public Event addEventAndModifyFollowingEvent ( Event event ) {
        PathCombination feasiblePaths = null;
        Event followingEvent = null;
        if ( scheduleManager.isEventOverlapFreeIntoSchedule( event, false ) ) {
            // CalculatePaths check feasibility into the schedule with regard of TIMETABLE
            // and CONSTRAINTS defined by the user
            feasiblePaths = pathManager.calculatePath( event, preferenceManager.getAllowedMeans( event, privateList ),
                    preferenceManager.getAllowedMeans( event, publicList ) );
            // If feasiblePaths is different from NULL there is a feasible solution and the event can be added.
            if ( feasiblePaths != null ) {
                // Setting previous location if prevLocChoice boolean param is true.
                if ( event.isPrevLocChoice() ) {
                    event.setDeparture( scheduleManager.getPossiblePreviousEvent( event.getStartingTime() )
                            .getEventLocation() );
                }
                event.setFeasiblePath( feasiblePaths.getPrevPath() );
                followingEvent = scheduleManager.getPossibleFollowingEvent( event.getStartingTime() );
                // Also info on the following event are uploaded, according to the calculated related-path.
                if ( followingEvent != null ) {
                    // Setting previous location if prevLocChoice boolean param is true.
                    if ( followingEvent.isPrevLocChoice() ) {
                        followingEvent.setDeparture( event.getEventLocation() );
                    }
                    followingEvent.setFeasiblePath( feasiblePaths.getFollPath() );
                }
            }
        }
        event.setScheduled( feasiblePaths != null );

        return followingEvent;
    }

    /**
     * Create all periodic events after an insertion of a periodic event ( recursively up to an year )
     *
     * @param event previous periodic event
     */
    public void propagatePeriodicEvents ( GenericEvent event ) {

        Instant upperBound = Instant.now().plus( 365, ChronoUnit.DAYS );

        if ( checkPropagationConditions( event, upperBound ) ) {
            GenericEvent nextEvent;
            nextEvent = event.nextPeriodicEvent();
            nextEvent.addEventAndModifyFollowingEvent( this );
            nextEvent.setUser( currentUser );
            nextEvent.save();
            nextEvent.addInUserList( currentUser );
            currentUser.save();
            startEventPropagatorThread( nextEvent );
        } else {
            //If the event is the last propagated one this knowledge is saved into the periodicity class
            event.getPeriodicity().setLastPropagatedEvent( event.getId() );
            event.getPeriodicity().save();
        }
    }

    /**
     * Checks if an event is to be propagated in time
     *
     * @param event event to be propagated
     * @param upperBound time upper bound until which the event is to be propagated
     * @return true if the event is to be propagated, false otherwise
     */
    private boolean checkPropagationConditions( GenericEvent event, Instant upperBound ){
        boolean condition1 = !event.getPeriodicity().getEndingDay().isBefore( event.getStartingTime() );
        Instant nextStartingTime = event.getStartingTime().plus( event.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS );
        boolean condition2 = nextStartingTime.isBefore( upperBound );
        return condition1 && condition2;
    }

    /**
     * Find a requested typeOfEvent instance
     *
     * @param name name of the requested typeOfEvent
     * @return the requested typeOfEvent
     */
    private TypeOfEvent findTypeOfEvent ( long name ) {
        return currentUser.getPreferences().stream()
                .filter( typeOfEvent -> typeOfEvent.getId() == name )
                .findFirst().get(); //NB his presence has to be already checked
    }

    /**
     * Create an event, starting from the received message
     *
     * @param eventMessage representation of the event to be added
     * @return the created event
     */
    private Event createEvent ( AddEventMessage eventMessage ) {
        TypeOfEvent type;
        if ( eventMessage.getIdTypeOfEvent() == 0 ) {
            type = new TypeOfEvent();
            type.setName( "emptyTypeOfEvent" );
            type.setParamFirstPath( ParamFirstPath.MIN_TIME );
            type.save();
        } else {
            type = findTypeOfEvent( eventMessage.getIdTypeOfEvent() );
        }
        Location departure = null;
        if ( !eventMessage.isPrevLocChoice() ) {
            departure = EventManager.findLocation( eventMessage.getDeparture() );
        }
        Location arrival = EventManager.findLocation( eventMessage.getEventLocation() );

        Event event = new Event( eventMessage.getName(), eventMessage.getStartingTime(), eventMessage.getEndingTime(),
                false, eventMessage.getDescription(), eventMessage.isPrevLocChoice(),
                eventMessage.isTravelAtLastChoice(), type, arrival, departure, null );


        if ( eventMessage.getPeriodicity() != null) {
            Period periodicity = createPeriodicity( eventMessage.getPeriodicity() );
            periodicity.save();
            event.setPeriodicity( periodicity );
        }
        return event;
    }

    /**
     * Create an event periodicity, starting from the received message
     *
     * @param periodMessage representation of the periodicity to be added
     * @return the created period
     */
    private Period createPeriodicity ( PeriodMessage periodMessage ) {
        return new Period( periodMessage.getStartingDay(), periodMessage.getEndingDay(), periodMessage.getDeltaDays() );
    }

    /**
     * Checks that the events fields are correct
     *
     * @param eventMessage message to be checked
     * @throws InvalidFieldException if some fields are wrong ( which one is written inside the exception class)
     */
    private void checkEventFields ( AddEventMessage eventMessage ) throws InvalidFieldException {
        List < String > errors = new ArrayList <>( checkGenericEventFields( eventMessage ) );
        if ( eventMessage.isPrevLocChoice() &&
                scheduleManager.getPossiblePreviousEvent( eventMessage.getStartingTime() ) == null )
            errors.add( "Not exists a previous location" );
        try {
            if ( eventMessage.getIdTypeOfEvent() != 0 ) {
                preferenceManager.getPreferencesProfile( eventMessage.getIdTypeOfEvent() );
            }
        } catch ( EntityNotFoundException e ) {
            errors.add( "TypeOfEvent not found" );
        }
        if ( errors.size() > 0 ) {
            throw new InvalidFieldException( errors );
        }
    }

    /**
     * Checks that the fields of a generic event are correct
     *
     * @param eventMessage message to be checked
     * @return a list of wrong fields ( list of strings )
     */
    private List < String > checkGenericEventFields ( AddGenericEventMessage eventMessage ) {
        List < String > genericEventErrors = new ArrayList <>();
        if ( eventMessage.getName() == null ) {
            genericEventErrors.add( "name" );
        }
        if ( !eventMessage.getStartingTime().isBefore( eventMessage.getEndingTime() ) ) {
            genericEventErrors.add( "starting time must be less than ending time" );
        }
        genericEventErrors.addAll( checkPeriodicity( eventMessage.getPeriodicity() ) );
        if ( eventMessage.getStartingTime().isBefore( Instant.now().plus( 10, ChronoUnit.SECONDS ) ) ) {
            genericEventErrors.add( "only future events are allowed" );
        }
        return genericEventErrors;
    }

    /**
     * Checks that the fields of a period message are correct
     *
     * @param periodMessage message to be checked
     * @return a list of wrong fields ( list of strings )
     */
    private List < String > checkPeriodicity ( PeriodMessage periodMessage ) {
        List < String > periodicityErrors = new ArrayList <>();
        if ( periodMessage != null){
            if ( !periodMessage.getStartingDay().isBefore( periodMessage.getEndingDay() ) ) {
                periodicityErrors.add( "in a periodic event starting day must be less than ending day" );
            }
            if ( periodMessage.getDeltaDays() < 0 ) {
                periodicityErrors.add( "deltaDays must be greater than zero" );
            }

            long deltaBetweenStartAndEndingTime = periodMessage.getStartingDay()
                    .until( periodMessage.getEndingDay(), ChronoUnit.DAYS );

            if ( deltaBetweenStartAndEndingTime < periodMessage.getDeltaDays() ) {
                periodicityErrors.add( "deltaDay value is less than the slack between start and end time" );
            }
        }
        return periodicityErrors;
    }

    /**
     * Modifies an already existent event
     *
     * @param eventMessage event representing the changed fields
     * @return the modified events
     * @throws InvalidFieldException   if some fields are wrong ( which one is written inside the exception class)
     * @throws EntityNotFoundException if the event to be modified does not exists
     */
    public List < Event > modifyEvent ( ModifyEventMessage eventMessage ) throws InvalidFieldException, EntityNotFoundException {
        checkEventFields( eventMessage );
        Event event = getEventInformation( eventMessage.getEventId() );
        deleteEvent( event.getId() );
        List < Event > eventsModified = addEvent( eventMessage );

        if ( eventMessage.isPropagateToPeriodicEvents() ) {
            //TODO handle periodic events
            //feature not included in the first release due to time-related issues
        }
        return eventsModified;
    }

    /**
     * Delete an event from the user's calendar
     *
     * @param id identifier of the event
     * @throws EntityNotFoundException if the event to be deleted does not exists
     */
    public void deleteEvent ( long id ) throws EntityNotFoundException {
        GenericEvent genericEvent;
        //TODO path of the following?
        try {
            genericEvent = getEventInformation( id );
            currentUser.removeEvent( id );
        } catch ( EntityNotFoundException e ) {
            genericEvent = getBreakEventInformation( id );
            currentUser.removeBreakEvent( id );
        }
        currentUser.save();
        genericEvent.remove();
    }

    /**
     * Allows to add a break event into the user's calendar
     *
     * @param eventMessage message that represents the event to be added
     * @return the inserted event
     * @throws InvalidFieldException if some fields are wrong ( which one is written inside the exception class)
     */
    public BreakEvent addBreakEvent ( AddBreakEventMessage eventMessage ) throws InvalidFieldException {
        checkBreakEventFields( eventMessage );
        //Create event, initially is not scheduled and non periodic
        BreakEvent breakEvent = createBreakEvent( eventMessage );
        addBreakEvent( breakEvent );
        breakEvent.setUser( currentUser );
        currentUser.addBreak( breakEvent );
        breakEvent.save();
        currentUser.save();
        startEventPropagatorThread( breakEvent ); //it handle periodic events
        return breakEvent;
    }

    /**
     * Method used in a visitor pattern in order to add break events into the user's calendar
     *
     * @param breakEvent break event to be added
     */
    public void addBreakEvent ( BreakEvent breakEvent ) {
        breakEvent.setScheduled( scheduleManager.isBreakOverlapFreeIntoSchedule( breakEvent, false ) );
    }

    /**
     * Checks the consistency of a break event's fields
     *
     * @param eventMessage message representing the break event
     * @throws InvalidFieldException if some fields are wrong ( which one is written inside the exception class)
     */
    private void checkBreakEventFields ( AddBreakEventMessage eventMessage ) throws InvalidFieldException {
        List < String > errors = new ArrayList <>();

        errors.addAll( checkGenericEventFields( eventMessage ) );

        if ( eventMessage.getMinimumTime() > eventMessage.getStartingTime()
                .until( eventMessage.getEndingTime(), ChronoUnit.SECONDS ) ) {
            errors.add( "minimum time must be less than the slack between start and ending time" );
        }
        if ( errors.size() > 0 ) {
            throw new InvalidFieldException( errors );
        }
    }

    /**
     * Create a break event instance starting from a break event message
     *
     * @param eventMessage message representing the event
     * @return the created break event
     */
    private BreakEvent createBreakEvent ( AddBreakEventMessage eventMessage ) {
        Period periodicity = createPeriodicity( eventMessage.getPeriodicity() );
        periodicity.save();
        return new BreakEvent( eventMessage.getName(), eventMessage.getStartingTime(), eventMessage.getEndingTime(),
                false, periodicity, eventMessage.getMinimumTime() );
    }

    /**
     * Modifies an already existent break event
     *
     * @param eventMessage message representing the break event to be modified
     * @return the modified break event
     * @throws InvalidFieldException   if some fields are wrong ( which one is written inside the exception class)
     * @throws EntityNotFoundException if the break event does not exist
     */
    public BreakEvent modifyBreakEvent ( ModifyBreakEventMessage eventMessage )
            throws InvalidFieldException, EntityNotFoundException {

        checkBreakEventFields( eventMessage );
        BreakEvent breakEvent = getBreakEventInformation( eventMessage.getEventId() );
        deleteEvent( breakEvent.getId() );
        breakEvent = createBreakEvent( eventMessage );
        if ( eventMessage.isPropagateToPeriodicEvents() ) {
            //TODO handle periodic events
            //feature not included in the first release due to time-related issues
        }

        return breakEvent;
    }

    /**
     * Starts a separate thread that will create all the periodic events up to an year
     * ( done in a separate thread due to heavy workload, especially with daily events )
     *
     * @param genericEvent event to be propagated in the calendar
     */
    private void startEventPropagatorThread ( GenericEvent genericEvent ) {
        if ( genericEvent.getPeriodicity() != null ) {
            periodicEventsExecutor.startEventPropagatorThread( genericEvent.getId(), currentUser.getEmail() );
        }
    }

    /**
     * Method called to initialize this bean with an authenticated user
     *
     * @param currentUser authenticated user class instance
     */
    @Override
    public void setCurrentUser ( User currentUser ) {
        this.currentUser = currentUser;
        this.scheduleManager.setCurrentUser( currentUser );
        this.preferenceManager.setCurrentUser( currentUser );
        this.pathManager.setCurrentUser( currentUser );
    }

    /**
     * Allows to obtain a location class instance, either loaded from database if it exist, or created otherwise
     *
     * @param locationMessage message that represent the location to be retrieved
     * @return the requested location
     */
    public static Location findLocation ( LocationMessage locationMessage ) {
        //TODO check correctness?
        Location location;
        try {
            location = Location.load(
                    new LocationId( locationMessage.getLatitude(), locationMessage.getLongitude() ) );
            return location;
        } catch ( EntityNotFoundException e ) {
            location = new Location( locationMessage.getLatitude(),
                    locationMessage.getLongitude(), locationMessage.getAddress() );
            location.save();
            return location;
        }
    }

}
