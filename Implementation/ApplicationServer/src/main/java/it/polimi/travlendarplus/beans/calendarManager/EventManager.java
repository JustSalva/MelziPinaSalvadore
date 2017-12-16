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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Stateless
public class EventManager extends UserManager {

    static TravelMeanEnum[] privateList = { TravelMeanEnum.CAR, TravelMeanEnum.BIKE, TravelMeanEnum.BY_FOOT };
    static TravelMeanEnum[] publicList = { TravelMeanEnum.TRAIN, TravelMeanEnum.BUS, TravelMeanEnum.TRAM,
            TravelMeanEnum.SUBWAY };

    @EJB
    private PreferenceManager preferenceManager;
    @EJB
    private ScheduleManager scheduleManager;
    @EJB
    private PathManager pathManager;

    @Inject
    private Executor executor;

    @PostConstruct
    public void postConstruct () {
        preferenceManager.setCurrentUser( currentUser );
        scheduleManager.setCurrentUser( currentUser );
        pathManager.setCurrentUser( currentUser );
    }

    public Event getEventInformation ( long id ) throws EntityNotFoundException {
        List < GenericEvent > eventList = new ArrayList <>( currentUser.getEvents() );
        return ( Event ) findEvent( eventList, id );
    }

    public BreakEvent getBreakEventInformation ( long id ) throws EntityNotFoundException {
        List < GenericEvent > eventList = new ArrayList <>( currentUser.getBreaks() );
        return ( BreakEvent ) findEvent( eventList, id );
    }

    private GenericEvent findEvent ( List < GenericEvent > eventList, long id ) throws EntityNotFoundException {
        GenericEvent requestedEvent = eventList.stream()
                .filter( event -> event.getId() == id ).findFirst().orElse( null );
        if ( requestedEvent == null )
            throw new EntityNotFoundException();
        return requestedEvent;
    }

    public List < GenericEvent > getEventsUpdated ( Instant timestampLocal ) {
        List < GenericEvent > updatedEvents = getEvents();

        updatedEvents = updatedEvents.stream()
                .filter( event -> event.getLastUpdate().getTimestamp().isAfter( timestampLocal ) )
                .collect( Collectors.toCollection( ArrayList::new ) );
        return updatedEvents;
    }

    public List < GenericEvent > getEvents () {
        List < GenericEvent > events = new ArrayList <>( currentUser.getEvents() );
        events.addAll( currentUser.getBreaks() );
        return events;
    }

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

    public void propagatePeriodicEvents ( GenericEvent event ) {

        Instant upperbound = Instant.now().plus( 365, ChronoUnit.DAYS );
        boolean condition1 = !event.getPeriodicity().getEndingDay().isBefore( event.getStartingTime() );
        Instant nextStartingTime = event.getStartingTime().plus( event.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS );
        boolean condition2 = nextStartingTime.isBefore( upperbound );
        if ( condition1 && condition2 ) {
            GenericEvent nextEvent;
            nextEvent = event.nextPeriodicEvent();
            nextEvent.addEventAndModifyFollowingEvent( this );
            nextEvent.setUser( currentUser );
            nextEvent.save();
            nextEvent.addInUserList( currentUser );
            currentUser.save();
            propagatePeriodicEvents( nextEvent );
        } else {
            //If the event is the last propagated one this knowledge is saved into the periodicity class
            event.getPeriodicity().setLastPropagatedEvent( event.getId() );
            event.getPeriodicity().save();
        }
    }

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

    private TypeOfEvent findTypeOfEvent ( long name ) {
        return currentUser.getPreferences().stream()
                .filter( typeOfEvent -> typeOfEvent.getId() == name )
                .findFirst().get(); //NB his presence has to be already checked
    }

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
        Period periodicity = createPeriodicity( eventMessage.getPeriodicity() );
        periodicity.save();
        Location arrival = EventManager.findLocation( eventMessage.getEventLocation() );
        return new Event( eventMessage.getName(), eventMessage.getStartingTime(), eventMessage.getEndingTime(),
                false, periodicity, eventMessage.getDescription(), eventMessage.isPrevLocChoice(),
                eventMessage.isTravelAtLastChoice(), type, arrival, departure );
    }

    private Period createPeriodicity ( PeriodMessage periodMessage ) {
        return new Period( periodMessage.getStartingDay(), periodMessage.getEndingDay(), periodMessage.getDeltaDays() );
    }

    private void checkEventFields ( AddEventMessage eventMessage ) throws InvalidFieldException {
        List < String > errors = new ArrayList <>();
        errors.addAll( checkGenericEventFields( eventMessage ) );
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


        /*private String eventLocation;
        private String departure;*/
        //TODO how to check that location String is correct?
        if ( errors.size() > 0 ) {
            throw new InvalidFieldException( errors );
        }
    }

    private List < String > checkGenericEventFields ( AddGenericEventMessage eventMessage ) {
        List < String > genericEventErrors = new ArrayList <>();
        if ( eventMessage.getName() == null ) {
            genericEventErrors.add( " name" );
        }
        if ( ! eventMessage.getStartingTime().isBefore( eventMessage.getEndingTime() ) ) {
            genericEventErrors.add( " starting time must be less than ending time" );
        }
        genericEventErrors.addAll( checkPeriodicity( eventMessage.getPeriodicity() ) );
        //TODO check if start and end time are in the past
        return genericEventErrors;
    }

    private List < String > checkPeriodicity ( PeriodMessage periodMessage ) {
        List < String > periodicityErrors = new ArrayList <>();

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

        return periodicityErrors;
    }

    public Event modifyEvent ( ModifyEventMessage eventMessage ) throws InvalidFieldException, EntityNotFoundException {
        checkEventFields( eventMessage );
        Event event = getEventInformation( eventMessage.getEventId() );
        //TODO set all new attributes
        //TODO it can be inserted in the schedule?
        //TODO ask and set the feasible path
        if ( eventMessage.isPropagateToPeriodicEvents() ) {
            //TODO handle periodic events ( delete all and recreate? )
        }
        //TODO add into either scheduled or not scheduled array and save!
        event.save();
        currentUser.save();
        return event;
    }


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

    public BreakEvent addBreakEvent ( AddBreakEventMessage eventMessage ) throws InvalidFieldException {
        checkBreakEventFields( eventMessage );
        //Create event, initially is not scheduled and non periodic
        BreakEvent breakEvent = createBreakEvent( eventMessage );
        addBreakEvent( breakEvent );
        breakEvent.setUser( currentUser );
        currentUser.addBreak( breakEvent );
        breakEvent.save();
        currentUser.save();
        startEventPropagatorThread( breakEvent );
        return breakEvent;   //it handle periodic events
    }

    public void addBreakEvent ( BreakEvent breakEvent ) {
        breakEvent.setScheduled( scheduleManager.isBreakOverlapFreeIntoSchedule( breakEvent, false ) );
    }

    private void checkBreakEventFields ( AddBreakEventMessage eventMessage ) throws InvalidFieldException {
        List < String > errors = new ArrayList <>();

        errors.addAll( checkGenericEventFields( eventMessage ) );

        if ( eventMessage.getMinimumTime() > eventMessage.getStartingTime()
                .until( eventMessage.getEndingTime(), ChronoUnit.SECONDS ) ) {
            errors.add( " minimum time must be less than the slack between start and ending time" );
        }
        if ( errors.size() > 0 ) {
            throw new InvalidFieldException( errors );
        }
    }

    private BreakEvent createBreakEvent ( AddBreakEventMessage eventMessage ) {
        Period periodicity = createPeriodicity( eventMessage.getPeriodicity() );
        periodicity.save();
        return new BreakEvent( eventMessage.getName(), eventMessage.getStartingTime(), eventMessage.getEndingTime(),
                false, periodicity, eventMessage.getMinimumTime() );
    }

    public BreakEvent modifyBreakEvent ( ModifyBreakEventMessage eventMessage )
            throws InvalidFieldException, EntityNotFoundException {

        checkBreakEventFields( eventMessage );
        BreakEvent breakEvent = getBreakEventInformation( eventMessage.getEventId() );
        //TODO set all new attributes
        //TODO it can be inserted in the schedule?
        if ( eventMessage.isPropagateToPeriodicEvents() ) {
            //TODO handle periodic events ( delete all and recreate? )
        }
        //TODO add into either scheduled or not scheduled array and save!
        breakEvent.save();
        return breakEvent;
    }

    private void startEventPropagatorThread ( GenericEvent genericEvent ) {
        if ( genericEvent.getPeriodicity() != null ) {
            /*PeriodicEventsRunnable runnable = new PeriodicEventsRunnable( this, genericEvent, currentUser );
            executor.execute( runnable );*/
            propagatePeriodicEvents( genericEvent );
        }
    }

    @Override
    public void setCurrentUser ( User currentUser ) {
        this.currentUser = currentUser;
        this.scheduleManager.setCurrentUser( currentUser );
        this.preferenceManager.setCurrentUser( currentUser );
        this.pathManager.setCurrentUser( currentUser );
    }

}
