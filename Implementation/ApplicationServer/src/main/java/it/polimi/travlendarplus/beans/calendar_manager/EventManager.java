package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.messages.calendarMessages.AddEventMessage;
import it.polimi.travlendarplus.messages.calendarMessages.ModifyEventMessage;

import javax.ejb.Stateless;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class EventManager extends UserManager{

    public Event getEventInformation( long id ) throws EntityNotFoundException{
        List<GenericEvent> eventList = new ArrayList<>( currentUser.getEvents() );
        return (Event) findEvent( eventList, id );
    }

    public BreakEvent getBreakEventInformation( long id ) throws EntityNotFoundException{
        List<GenericEvent> eventList = new ArrayList<>( currentUser.getBreaks() );
        return (BreakEvent) findEvent( eventList, id );
    }

    private GenericEvent findEvent( List<GenericEvent> eventList, long id) throws EntityNotFoundException{
        GenericEvent requestedEvent = eventList.stream()
                .filter( event -> event.getId() == id ).findFirst().orElse( null );
        if ( requestedEvent == null )
            throw new EntityNotFoundException();
        return requestedEvent;
    }

    public List<GenericEvent> getEventUpdated( Instant timestampLocal){
        List<GenericEvent> updatedEvents = new ArrayList<>(  currentUser.getEvents() );

        updatedEvents.addAll( currentUser.getBreaks() );

        updatedEvents = updatedEvents.stream()
                .filter( event -> event.getLastUpdate().getTimestamp().isAfter( timestampLocal ) )
                .collect( Collectors.toCollection( ArrayList::new));
        return updatedEvents;
    }

    public long addEvent( AddEventMessage eventMessage) throws InvalidFieldException{
        checkEventFields( eventMessage );
        //Create event, initially is not scheduled
        Event event = createEvent( eventMessage );
        //TODO it can be inserted in the schedule?
        //TODO handle periodic events
        //TODO ask and set the feasible path
        //TODO add into either scheduled or not scheduled array and save!
        return event.getId();
    }

    private Location findLocation( String address ){
        //TODO
        return new Location( );
    }

    private TypeOfEvent findTypeOfEvent( String name){
        return currentUser.getPreferences().stream()
                .filter( typeOfEvent -> typeOfEvent.getName().equals( name ) )
                .findFirst().get(); //NB his presence has to be already checked
    }

    private Event createEvent(AddEventMessage eventMessage){
        TypeOfEvent type = findTypeOfEvent( eventMessage.getTypeOfEvent() );
        Location departure = findLocation( eventMessage.getDeparture() );
        Location arrival = findLocation( eventMessage.getEventLocation() );
        return new Event( eventMessage.getName(), eventMessage.getStartingTime(), eventMessage.getEndingTime(),
                false, null, eventMessage.getDescription(), eventMessage.isPrevLocChoice(), type,
                arrival, departure);
    }

    private void checkEventFields ( AddEventMessage eventMessage) throws InvalidFieldException {
        //TODO check the message consistency, write in the error which field/fields are invalid
    }

    public void modifyEvent( ModifyEventMessage eventMessage) throws InvalidFieldException, EntityNotFoundException{
        checkEventFields( eventMessage );
        Event event = getEventInformation( eventMessage.getEventId() );
        //TODO set all new attributes
        //TODO it can be inserted in the schedule?
        //TODO handle periodic events
        //TODO ask and set the feasible path
        //TODO add into either scheduled or not scheduled array and save!
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


}
