package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities.PathCombination;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.messages.GenericMessage;
import it.polimi.travlendarplus.messages.calendarMessages.eventMessages.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class EventManager extends UserManager{

    static TravelMeanEnum[] privateList = {TravelMeanEnum.CAR, TravelMeanEnum.BIKE, TravelMeanEnum.BY_FOOT};
    static TravelMeanEnum[] publicList = {TravelMeanEnum.TRAIN, TravelMeanEnum.BUS, TravelMeanEnum.TRAM, TravelMeanEnum.SUBWAY};

    @EJB
    private PreferenceManager preferenceManager;
    private ScheduleManager scheduleManager;
    private PathManager pathManager;

    @PostConstruct
    public void postConstruct() {
        preferenceManager.setCurrentUser( currentUser );
        scheduleManager.setCurrentUser(currentUser);
        pathManager.setCurrentUser(currentUser);
    }

    public Event getEventInformation( long id ) throws EntityNotFoundException{
        List<GenericEvent> eventList = new ArrayList<>( currentUser.getEvents() );
        return (Event) findEvent( eventList, id );
    }

    public BreakEvent getBreakEventInformation( long id ) throws EntityNotFoundException{
        List<GenericEvent> eventList = new ArrayList<>( currentUser.getBreaks() );
        return (BreakEvent) findEvent( eventList, id );
    }

    private GenericEvent findEvent( List<GenericEvent> eventList, long id ) throws EntityNotFoundException{
        GenericEvent requestedEvent = eventList.stream()
                .filter( event -> event.getId() == id ).findFirst().orElse( null );
        if ( requestedEvent == null )
            throw new EntityNotFoundException();
        return requestedEvent;
    }

    public List< GenericEvent > getEventsUpdated( Instant timestampLocal ){
        List< GenericEvent > updatedEvents = getEvents();

        updatedEvents = updatedEvents.stream()
                .filter( event -> event.getLastUpdate().getTimestamp().isAfter( timestampLocal ) )
                .collect( Collectors.toCollection( ArrayList::new));
        return updatedEvents;
    }

    public List< GenericEvent > getEvents(){
        List< GenericEvent > events = new ArrayList<>(  currentUser.getEvents() );
        events.addAll( currentUser.getBreaks() );
        return events;
    }

    public List < GenericEvent > addEvent( AddEventMessage eventMessage ) throws InvalidFieldException{
        PathCombination feasiblePaths = null;
        checkEventFields( eventMessage );
        //Create event, initially is not scheduled and non periodic
        Event event = createEvent( eventMessage );
        if(scheduleManager.isEventOverlapFreeIntoSchedule(event, false)) {
            // CalculatePaths check feasibility into the schedule with regard of TIMETABLE and CONSTRAINTS defined by the user
            feasiblePaths = pathManager.calculatePath(event, preferenceManager.getAllowedMeans(event, privateList),
                    preferenceManager.getAllowedMeans(event, publicList), false);
            // If feasiblePaths id different from NULL there is a feasible solution and the event can be added.
            if(feasiblePaths != null) {
                event.setFeasiblePath(feasiblePaths.getPrevPath());
                Event followingEvent = scheduleManager.getPossibleFollowingEvent(event);
                // Also info on the following event are uploaded, according to the calculated related-path.
                if (followingEvent != null) {
                    //TODO set departure according to boolean on prev location
                    followingEvent.setDeparture(null); //TODO
                    followingEvent.setFeasiblePath(feasiblePaths.getFollPath());
                    followingEvent.save();
                }
            }
        }
        event.setScheduled(feasiblePaths != null);
        event.save();
        currentUser.save();
        return propagatePeriodicEvents( event ); //it handle periodic events
    }

    public List < GenericEvent > propagatePeriodicEvents( GenericEvent event ){
        List < GenericEvent > propagatedEvents = new ArrayList<>( );
        propagatedEvents.add( event );
        Instant upperbound = Instant.now().plus( 1, ChronoUnit.YEARS );
        GenericEvent nextEvent;
        if( ! event.getPeriodicity().getEndingDay().isAfter( event.getStartingTime() )
                && event.getStartingTime().plus( event.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS )
                .isBefore( upperbound )){

            nextEvent = event.nextPeriodicEvent();

            if ( event.isScheduled() ){
                //TODO check nextEventFeasibility with copied travel
            }else{
                //TODO if event is not scheduled a path must be computed
            }
            nextEvent.save();
            nextEvent.addInUserList( currentUser );
            currentUser.save();
            propagatedEvents.addAll( propagatePeriodicEvents( nextEvent ) );
        }else{
            //If the event is the last propagated one this knowledge is saved into the periodicity class
            event.getPeriodicity().setLastPropagatedEvent( event.getId() );
            event.getPeriodicity().save();
        }
        return propagatedEvents;

    }

    private Location findLocation( String address ){
        //TODO create if not present
        return new Location( );
    }

    private TypeOfEvent findTypeOfEvent( long name ){
        return currentUser.getPreferences().stream()
                .filter( typeOfEvent -> typeOfEvent.getId() == name )
                .findFirst().get(); //NB his presence has to be already checked
    }

    private Event createEvent( AddEventMessage eventMessage ){
        TypeOfEvent type = findTypeOfEvent( eventMessage.getIdTypeOfEvent() );
        Location departure = findLocation( eventMessage.getDeparture() );
        Location arrival = findLocation( eventMessage.getEventLocation() );
        return new Event( eventMessage.getName(), eventMessage.getStartingTime(), eventMessage.getEndingTime(),
                false, null, eventMessage.getDescription(), eventMessage.isPrevLocChoice(), type,
                arrival, departure);
    }

    private void checkEventFields ( AddEventMessage eventMessage ) throws InvalidFieldException {
        List<String> errors = new ArrayList<>( );

        errors.addAll( checkGenericEventFields( eventMessage ) );

        try {
            preferenceManager.getPreferencesProfile( eventMessage.getIdTypeOfEvent() );
        } catch ( EntityNotFoundException e ) {
            errors.add( "TypeOfEvent not found" );
        }
        /*private String eventLocation;
        private String departure;*/
        //TODO how to check that location String is correct?
        if( errors.size() > 0 ){
            throw new InvalidFieldException( errors );
        }
    }

    private List< String > checkGenericEventFields ( AddGenericEventMessage eventMessage ){
        List<String> genericEventErrors = new ArrayList<>( );
        if (eventMessage.getName() == null ){
            genericEventErrors.add( " name" );
        }
        if ( ! eventMessage.getStartingTime().isBefore( eventMessage.getEndingTime() ) ){
            genericEventErrors.add( " starting time must be less than ending time" );
        }
        genericEventErrors.addAll( checkPeriodicity( eventMessage.getPeriodicity() ) );

        return genericEventErrors;
    }

    private List< String > checkPeriodicity ( PeriodMessage periodMessage ){
        List<String> periodicityErrors = new ArrayList<>( );

        if ( ! periodMessage.getStartingDay().isBefore( periodMessage.getEndingDay() ) ){
            periodicityErrors.add( "in a periodic event starting day must be less than ending day" );
        }
        if ( periodMessage.getDeltaDays() < 0 ){
            periodicityErrors.add( "deltaDays must be greater than zero" );
        }

        long deltaBetweenStartAndEndingTime = periodMessage.getStartingDay()
                .until( periodMessage.getEndingDay(), ChronoUnit.DAYS  );

        if ( deltaBetweenStartAndEndingTime > periodMessage.getDeltaDays() ){
            periodicityErrors.add( "deltaDay value is less than the slack between start and end time" );
        }

        return periodicityErrors;
    }

    public Event modifyEvent( ModifyEventMessage eventMessage) throws InvalidFieldException, EntityNotFoundException{
        checkEventFields( eventMessage );
        Event event = getEventInformation( eventMessage.getEventId() );
        //TODO set all new attributes
        //TODO it can be inserted in the schedule?
        //TODO ask and set the feasible path
        if ( eventMessage.isPropagateToPeriodicEvents() ){
            //TODO handle periodic events ( delete all and recreate? )
        }
        //TODO add into either scheduled or not scheduled array and save!
        event.save();
        currentUser.save();
        return event;
    }


    public void deleteEvent( long id ) throws EntityNotFoundException{
        GenericEvent genericEvent;
        try {
            genericEvent = getEventInformation( id );
        } catch ( EntityNotFoundException e ) {
                genericEvent = getBreakEventInformation( id );
        }
        genericEvent.remove();
    }

    public List< GenericEvent > addBreakEvent( AddBreakEventMessage eventMessage) throws InvalidFieldException{
        checkBreakEventFields( eventMessage );
        //Create event, initially is not scheduled and non periodic
        BreakEvent breakEvent = createBreakEvent( eventMessage );
        breakEvent.setScheduled(scheduleManager.isBreakOverlapFreeIntoSchedule(breakEvent, false));
        breakEvent.save();
        currentUser.save();
        return propagatePeriodicEvents( breakEvent );   //it handle periodic events
    }

    private void checkBreakEventFields ( AddBreakEventMessage eventMessage) throws InvalidFieldException {
        List<String> errors = new ArrayList<>( );

        errors.addAll( checkGenericEventFields( eventMessage ) );

        if ( eventMessage.getMinimumTime() > eventMessage.getStartingTime()
                .until( eventMessage.getEndingTime(), ChronoUnit.SECONDS  )){
            errors.add( " minimum time must be less than the slack between start and ending time" );
        }
        if( errors.size() > 0 ){
            throw new InvalidFieldException( errors );
        }
    }
    private BreakEvent createBreakEvent(AddBreakEventMessage eventMessage){
        return new BreakEvent( eventMessage.getName(), eventMessage.getStartingTime(), eventMessage.getEndingTime(),
                false, null, eventMessage.getMinimumTime());
    }

    public BreakEvent modifyBreakEvent( ModifyBreakEventMessage eventMessage)
            throws InvalidFieldException, EntityNotFoundException{

        checkBreakEventFields( eventMessage );
        BreakEvent breakEvent = getBreakEventInformation( eventMessage.getEventId() );
        //TODO set all new attributes
        //TODO it can be inserted in the schedule?
        if ( eventMessage.isPropagateToPeriodicEvents() ){
            //TODO handle periodic events ( delete all and recreate? )
        }
        //TODO add into either scheduled or not scheduled array and save!
        breakEvent.save();
        return breakEvent;
    }

    @Override
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        this.scheduleManager.setCurrentUser(currentUser);
        this.preferenceManager.setCurrentUser(currentUser);
        this.pathManager.setCurrentUser(currentUser);
    }

}
