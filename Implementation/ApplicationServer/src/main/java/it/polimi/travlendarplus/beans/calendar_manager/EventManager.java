package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
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

    @EJB
    private PreferenceManager preferenceManager;

    @PostConstruct
    public void postConstruct() {
        preferenceManager.setCurrentUser( currentUser );
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
        checkEventFields( eventMessage );
        //Create event, initially is not scheduled and non periodic
        Event event = createEvent( eventMessage );
        //TODO it can be inserted in the schedule?
        //TODO ask and set the feasible path
        //TODO add into either scheduled or not scheduled array and save!
        event.save();
        currentUser.save();
        return propagatePeriodicEvents( event ); //it handle periodic events
    }

    public List < GenericEvent > propagatePeriodicEvents( GenericEvent event ){
        List < GenericEvent > propagatedEvents = new ArrayList<>( );
        propagatedEvents.add( event );
        Instant upperbound = Instant.now().plus( 1, ChronoUnit.YEARS ); //TODO change into exact time
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
        //TODO handle periodic events
        //TODO ask and set the feasible path
        //TODO add into either scheduled or not scheduled array and save!
        event.save();
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
        //TODO it can be inserted in the schedule?
        //TODO handle periodic events
        //TODO add into either scheduled or not scheduled array and save!
        breakEvent.save();
        currentUser.save();
        return propagatePeriodicEvents( breakEvent );
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
        //TODO handle periodic events
        //TODO add into either scheduled or not scheduled array and save!
        breakEvent.save();
        return breakEvent;
    }



}
