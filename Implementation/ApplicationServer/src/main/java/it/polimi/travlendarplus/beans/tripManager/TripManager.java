package it.polimi.travlendarplus.beans.tripManager;

import it.polimi.travlendarplus.RESTful.messages.tripMessages.*;
import it.polimi.travlendarplus.beans.calendarManager.EventManager;
import it.polimi.travlendarplus.beans.calendarManager.UserManager;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.tickets.*;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.WrongFields;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.IncompatibleTravelMeansException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.TicketNotValidException;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provide all methods related to handle trips - arranging functionalities
 */
@Stateless
public class TripManager extends UserManager {

    /**
     * @return all the tickets saved by the user
     */
    public List < Ticket > getTickets () {
        return currentUser.getHeldTickets();
    }

    /**
     * Provide info of a specific ticket
     *
     * @param ticketId identifier of the requested ticket
     * @return the requested ticket
     * @throws EntityNotFoundException if the requested ticket does not exist
     */
    public Ticket getTicket ( long ticketId ) throws EntityNotFoundException {
        List < Ticket > heldTickets = getTickets();
        Ticket requested = heldTickets.stream()
                .filter( ticket -> ticket.getId() == ticketId )
                .findFirst().orElse( null );
        if ( requested == null ) {
            throw new EntityNotFoundException( "the ticket doesn't exists" );
        }
        return requested;
    }

    /**
     * Finds all the user's tickets that can be applicable to the specified travel component
     *
     * @param travelComponentId identifier of the travel component to be added
     * @return all the tickets saved by the user
     * @throws EntityNotFoundException if the specified travel component does not exist
     */
    public List < Ticket > getCompatibleTickets( long travelComponentId ) throws EntityNotFoundException {

        TravelComponent travelComponent = retrieveUsersTravelComponent( travelComponentId );
        List < Ticket > userTickets = getTickets();
        List < Ticket > compatibleTickets = new ArrayList <>();
        //the ticket list have to be scanned in order to find the compatible tickets
        for ( Ticket ticket : userTickets ){
            if( ticket.isCompatible( travelComponent.getMeanUsed().getType() )){
                try {
                    ticket.checkTicketValidityAfterTravelAssociation( travelComponent );
                    //if compatible the ticket is added into the list of compatible ones
                    compatibleTickets.add( ticket );
                } catch ( TicketNotValidException e ) {
                    //if not compatible nothing happens
                }
            }
        }
        return compatibleTickets;

    }

    /**
     * Adds a distance ticket in the user's profile
     *
     * @param distanceTicketMessage message representing the distance ticket
     * @return the saved distance ticket class
     * @throws InvalidFieldException if one or more fields of the message are invalid
     * @see DistanceTicket
     */
    public DistanceTicket addDistanceTicket ( AddDistanceTicketMessage distanceTicketMessage )
            throws InvalidFieldException {

        checkDistanceTicketConsistency( distanceTicketMessage );
        DistanceTicket distanceTicket = createDistanceTicket( distanceTicketMessage );
        addTicketToUserAndSave( distanceTicket );
        return distanceTicket;
    }

    /**
     * Given a distance ticket message creates the relative distance ticket
     *
     * @param distanceTicketMessage message representing the instance of distance ticket to be created
     * @return the requested distance ticket
     * @see DistanceTicket
     */
    public DistanceTicket createDistanceTicket ( AddDistanceTicketMessage distanceTicketMessage ) {
        return new DistanceTicket( distanceTicketMessage.getCost(),
                createListOfPublicTravelMean( distanceTicketMessage ),
                distanceTicketMessage.getDistance() );
    }

    /**
     * Given a generic ticket message creates the list of connected public travel means
     *
     * @param addTicketMessage the message containing the ticket info
     * @return the requested list of public travel means
     * @see PublicTravelMean
     */
    private ArrayList < PublicTravelMean > createListOfPublicTravelMean ( AddTicketMessage addTicketMessage ) {
        return addTicketMessage.getRelatedTo().stream()
                .map( message -> new PublicTravelMean( message.getName(), message.getType() ) )
                .collect( Collectors.toCollection( ArrayList::new ) );
    }

    /**
     * Adds the ticket into the user's profile
     *
     * @param ticket generic ticket to be added
     */
    private void addTicketToUserAndSave ( Ticket ticket ) {
        ticket.save();
        currentUser.addTicket( ticket );
        currentUser.save();
    }

    /**
     * Adds a generic ticket in the user's profile
     *
     * @param genericTicketMessage message representing the generic ticket
     * @return the saved generic ticket class
     * @throws InvalidFieldException if one or more fields of the message are invalid
     * @see GenericTicket
     */
    public GenericTicket addGenericTicket ( AddGenericTicketMessage genericTicketMessage )
            throws InvalidFieldException {

        checkGenericTicketConsistency( genericTicketMessage );
        GenericTicket genericTicket = createGenericTicket( genericTicketMessage );
        addTicketToUserAndSave( genericTicket );
        return genericTicket;
    }

    /**
     * Given a generic ticket message creates the relative generic ticket
     *
     * @param genericTicketMessage message representing the instance of distance ticket to be created
     * @return the requested generic ticket
     * @see GenericTicket
     */
    public GenericTicket createGenericTicket ( AddGenericTicketMessage genericTicketMessage ) {
        return new GenericTicket( genericTicketMessage.getCost(),
                createListOfPublicTravelMean( genericTicketMessage ),
                genericTicketMessage.getLineName() );
    }

    /**
     * Adds a path ticket in the user's profile
     *
     * @param pathTicketMessage message representing the path ticket
     * @return the saved path ticket class
     * @throws InvalidFieldException if one or more fields of the message are invalid
     * @see PathTicket
     */
    public PathTicket addPathTicket ( AddPathTicketMessage pathTicketMessage )
            throws InvalidFieldException {

        checkPathTicketConsistency( pathTicketMessage );
        PathTicket pathTicket = createPathTicket( pathTicketMessage );
        pathTicket.getStartingLocation().save();
        pathTicket.getEndingLocation().save();
        addTicketToUserAndSave( pathTicket );
        return pathTicket;
    }

    /**
     * Given a path ticket message creates the relative path ticket
     *
     * @param pathTicketMessage message representing the instance of path ticket to be created
     * @return the requested path ticket
     * @see PathTicket
     */
    public PathTicket createPathTicket ( AddPathTicketMessage pathTicketMessage ) {
        Location departure = EventManager.findLocation( pathTicketMessage.getStartingLocation() );
        Location arrival = EventManager.findLocation( pathTicketMessage.getEndingLocation() );
        return new PathTicket( pathTicketMessage.getCost(),
                createListOfPublicTravelMean( pathTicketMessage ),
                pathTicketMessage.getLineName(), departure, arrival );
    }

    /**
     * Adds a period ticket in the user's profile
     *
     * @param periodTicketMessage message representing the period ticket
     * @return the saved period ticket class
     * @throws InvalidFieldException if one or more fields of the message are invalid
     * @see PeriodTicket
     */
    public PeriodTicket addPeriodTicket ( AddPeriodTicketMessage periodTicketMessage )
            throws InvalidFieldException {

        Ticket decorator = checkPeriodTicketConsistency( periodTicketMessage );
        PeriodTicket periodTicket = createPeriodTicket( periodTicketMessage, decorator );
        addTicketToUserAndSave( periodTicket );
        return periodTicket;
    }

    /**
     * Given a period ticket message and his decorated ticket creates the relative period ticket
     *
     * @param periodTicketMessage message representing the instance of period ticket to be created
     * @param decorator generic ticket that have to be decorated by the path ticket
     * @return the requested period ticket
     * @see PathTicket
     */
    private PeriodTicket createPeriodTicket ( AddPeriodTicketMessage periodTicketMessage, Ticket decorator ) {

        return new PeriodTicket( periodTicketMessage.getCost(), createListOfPublicTravelMean( periodTicketMessage ),
                periodTicketMessage.getName(), periodTicketMessage.getStartingDate(),
                periodTicketMessage.getEndingDate(), decorator );
    }

    /**
     * Deletes a ticket from the user's profile
     *
     * @param ticketId identifier of the ticket to be removed
     * @throws EntityNotFoundException if the ticket to-be-deleted does not exist
     */
    public void deleteTicket ( long ticketId ) throws EntityNotFoundException {
        Ticket ticket = getTicket( ticketId );
        currentUser.deleteTicket( ticketId );
        currentUser.save();
        ticket.remove();
    }

    /**
     * Mark a ticket as to-be-used in a specific travel
     *
     * @param ticketId identifier of the ticket to be selected
     * @param travelComponentId identifier of the travel the ticket is to be connected with
     * @throws EntityNotFoundException if either the ticket or the travel specified does not exist
     * @throws IncompatibleTravelMeansException if the ticket is incompatible with the specified travel
     * @throws TicketNotValidException if the ticket can't be linked to the given travel component
     *                                 ( why is explained inside the exception instance )
     */
    public void selectTicket ( long ticketId, long travelComponentId )
            throws EntityNotFoundException, IncompatibleTravelMeansException, TicketNotValidException {

        TravelComponent travelComponent = retrieveUsersTravelComponent( travelComponentId );
        Ticket selectedTicket = getTicket( ticketId );
        selectedTicket.checkTicketValidityAfterTravelAssociation( travelComponent );
        selectedTicket.addLinkedTravel( travelComponent );
        selectedTicket.save();
    }

    /**
     * Deselect a ticket, previously marked as to-be-used in a specific travel
     *
     * @param ticketId identifier of the ticket to be deselected
     * @param travelComponentId identifier of the travel the ticket is to be connected with
     * @throws EntityNotFoundException if either the ticket or the travel specified does not exist
     */
    public void deselectTicket ( long ticketId, long travelComponentId ) throws EntityNotFoundException {
        Ticket ticket = getTicket( ticketId );

        //Checks travel component existence, throw exception if not
        retrieveUsersTravelComponent( travelComponentId );

        ticket.removeLinkedTravel( travelComponentId );
        ticket.save();
    }

    /**
     * Provides the class containing the specified travel component, retrieving it from the database
     *
     * @param travelComponentId identifier of the travel component to be found
     * @return the requested travel component
     * @throws EntityNotFoundException if the travel requested does not exist
     */
    private TravelComponent retrieveUsersTravelComponent ( long travelComponentId ) throws EntityNotFoundException {
        TravelComponent userSingleTravel = currentUser.getEvents()
                .stream().filter( event -> event.getFeasiblePath() != null )
                .map( Event::getFeasiblePath )
                .flatMap( travel -> travel.getMiniTravels().stream() )
                .filter( travelComponent -> travelComponent.getId() == travelComponentId )
                .findFirst().orElse( null );
        if ( userSingleTravel == null ) {
            throw new EntityNotFoundException( "the travel doesn't exists" );
        }
        return userSingleTravel;
    }

    /**
     * Checks that the fields of a distanceTicketMessage are correct
     *
     * @param distanceTicketMessage message to be checked
     * @throws InvalidFieldException if some fields are wrong ( which one is specified in the error class )
     */
    public void checkDistanceTicketConsistency ( AddDistanceTicketMessage distanceTicketMessage )
            throws InvalidFieldException {

        List < String > errors = checkTicketConsistency( distanceTicketMessage );
        if ( distanceTicketMessage.getDistance() <= 0 ) {
            errors.add( WrongFields.DISTANCE );
        }
        checkErrorExistence( errors );
    }

    /**
     * Checks that the fields of a genericTicketMessage are correct
     *
     * @param genericTicketMessage message to be checked
     * @throws InvalidFieldException if some fields are wrong ( which one is specified in the error class )
     */
    public void checkGenericTicketConsistency ( AddGenericTicketMessage genericTicketMessage )
            throws InvalidFieldException {

        List < String > errors = checkTicketConsistency( genericTicketMessage );
        if ( genericTicketMessage.getLineName() == null ) {
            errors.add( WrongFields.LINE_NAME );
        }
        checkErrorExistence( errors );
    }

    /**
     * Checks that the fields of a pathTicketMessage are correct
     *
     * @param pathTicketMessage message to be checked
     * @throws InvalidFieldException if some fields are wrong ( which one is specified in the error class )
     */
    public void checkPathTicketConsistency ( AddPathTicketMessage pathTicketMessage )
            throws InvalidFieldException {

        List < String > errors = new ArrayList <>();
        try {
            checkGenericTicketConsistency( pathTicketMessage );
        } catch ( InvalidFieldException e ) {
            errors.addAll( e.getInvalidFields() );
        }
        checkErrorExistence( errors );
    }


    /**
     * Checks that the fields of a periodTicketMessage are correct
     *
     * @param periodTicketMessage message to be checked
     * @throws InvalidFieldException if some fields are wrong ( which one is specified in the error class )
     */
    private Ticket checkPeriodTicketConsistency ( AddPeriodTicketMessage periodTicketMessage )
            throws InvalidFieldException {

        List < String > errors = checkTicketConsistency( periodTicketMessage );
        Ticket decorator = null;
        if ( periodTicketMessage.getName() == null ) {
            errors.add( WrongFields.NAME );
        }
        if ( !periodTicketMessage.getStartingDate().isBefore( periodTicketMessage.getEndingDate() ) ) {
            errors.add( WrongFields.MIN_TIME_MUST_BE_LESS_THAN_ENDING_TIME );
        }
        if ( periodTicketMessage.isConsistent() ) {
            try {
                if ( periodTicketMessage.isDistanceDecorator() ) {
                    decorator = periodTicketMessage.retrieveDistanceTicket( this );
                } else if ( periodTicketMessage.isGenericDecorator() ) {
                    decorator = periodTicketMessage.retrieveGenericTicket( this );
                } else if ( periodTicketMessage.isPathDecorator() ) {
                    decorator = periodTicketMessage.retrievePathTicket( this );
                }
            } catch ( InvalidFieldException e ) {
                errors.addAll( e.getInvalidFields() );
            }
        } else {
            errors.add( WrongFields.INVALID_TICKET_DECORATOR );
        }
        checkErrorExistence( errors );
        //NB this decorator cannot be null cause if it is an InvalidFieldException
        // is thrown by the precedent instruction
        return decorator;
    }

    /**
     * Checks the consistency of the fields that are shared by all ticket messages ( common super class )
     *
     * @param addTicketMessage generic ticket message to be checked
     * @return a list of wrong fields, if any, or an empty list otherwise
     */
    private List < String > checkTicketConsistency ( AddTicketMessage addTicketMessage ) {
        List < String > errors = new ArrayList <>();
        if ( addTicketMessage.getCost() < 0 ) {
            errors.add( WrongFields.COST );
        }
        for ( AddPublicTravelMeanMessage meanMessage : addTicketMessage.getRelatedTo() ) {
            errors.addAll( checkPublicTravelMeanConsistency( meanMessage ) );
        }
        return errors;
    }

    /**
     * Checks the consistency of the fields of a message containing info about
     * a specific travel mean compatible with a ticket
     *
     * @param meanMessage message containing the info of the travel mean
     * @return a list of wrong fields, if any, or an empty list otherwise
     */
    private List < String > checkPublicTravelMeanConsistency ( AddPublicTravelMeanMessage meanMessage ) {
        List < String > errors = new ArrayList <>();
        if ( meanMessage.getName() == null ) {
            errors.add( WrongFields.TRAVEL_MEAN + WrongFields.NAME );
        }
        if ( !TravelMeanEnum.isPublicTravelMean( meanMessage.getType() ) ) {
            errors.add( WrongFields.TRAVEL_MEAN + WrongFields.TYPE );
        }
        return errors;
    }

    /**
     * Checks that an array of errors description is empty or not
     *
     * @param errors list that might contains all possible errors
     * @throws InvalidFieldException if the list actually contains one or more errors,
     * which one is specified inside the exception thrown
     */
    private void checkErrorExistence ( List < String > errors ) throws InvalidFieldException {
        if ( errors.size() > 0 ) {
            throw new InvalidFieldException( errors );
        }
    }

    /**
     * Modifies a distance ticket that is already present in the user's profile
     *
     * @param distanceTicketMessage message representing the info that are to be updated
     * @param ticketId identifier of the ticket that is to be updated
     * @return the updated distance ticket instance
     * @throws InvalidFieldException if some fields are wrong ( which one is specified in the error class )
     * @throws EntityNotFoundException if the ticket to-be-modified does not exist
     * @throws IncompatibleTravelMeansException if after the update one or more public travel means would become incompatible
     */
    public DistanceTicket modifyDistanceTicket ( AddDistanceTicketMessage distanceTicketMessage, long ticketId )
            throws InvalidFieldException, EntityNotFoundException, IncompatibleTravelMeansException {

        List < TravelComponent > linkedTravels = retrieveTravelsLinked( ticketId );
        DistanceTicket newDistanceTicket = addDistanceTicket( distanceTicketMessage );
        addLinkedTravels( linkedTravels, newDistanceTicket );
        deleteTicket( ticketId );
        return newDistanceTicket;
    }

    /**
     * Modifies a generic ticket that is already present in the user's profile
     *
     * @param genericTicketMessage message representing the info that are to be updated
     * @param ticketId identifier of the ticket that is to be updated
     * @return the updated generic ticket instance
     * @throws InvalidFieldException if some fields are wrong ( which one is specified in the error class )
     * @throws EntityNotFoundException if the ticket to-be-modified does not exist
     * @throws IncompatibleTravelMeansException if after the update one or more public travel means would become incompatible
     */
    public GenericTicket modifyGenericTicket ( AddGenericTicketMessage genericTicketMessage, long ticketId )
            throws InvalidFieldException, EntityNotFoundException, IncompatibleTravelMeansException {

        List < TravelComponent > linkedTravels = retrieveTravelsLinked( ticketId );
        GenericTicket newGenericTicket = addGenericTicket( genericTicketMessage );
        addLinkedTravels( linkedTravels, newGenericTicket );
        deleteTicket( ticketId );
        return newGenericTicket;
    }

    /**
     * Modifies a path ticket that is already present in the user's profile
     *
     * @param pathTicketMessage message representing the info that are to be updated
     * @param ticketId identifier of the ticket that is to be updated
     * @return the updated path ticket instance
     * @throws InvalidFieldException if some fields are wrong ( which one is specified in the error class )
     * @throws EntityNotFoundException if the ticket to-be-modified does not exist
     * @throws IncompatibleTravelMeansException if after the update one or more public travel means would become incompatible
     */
    public PathTicket modifyPathTicket ( AddPathTicketMessage pathTicketMessage, long ticketId )
            throws InvalidFieldException, EntityNotFoundException, IncompatibleTravelMeansException {

        List < TravelComponent > linkedTravels = retrieveTravelsLinked( ticketId );
        PathTicket newPathTicket = addPathTicket( pathTicketMessage );
        addLinkedTravels( linkedTravels, newPathTicket );
        deleteTicket( ticketId );
        return newPathTicket;
    }

    /**
     * Modifies a period ticket that is already present in the user's profile
     *
     * @param periodTicketMessage message representing the info that are to be updated
     * @param ticketId identifier of the ticket that is to be updated
     * @return the updated period ticket instance
     * @throws InvalidFieldException if some fields are wrong ( which one is specified in the error class )
     * @throws EntityNotFoundException if the ticket to-be-modified does not exist
     * @throws IncompatibleTravelMeansException if after the update one or more public travel means would become incompatible
     */
    public PeriodTicket modifyPeriodTicket ( AddPeriodTicketMessage periodTicketMessage, long ticketId )
            throws InvalidFieldException, EntityNotFoundException, IncompatibleTravelMeansException {

        List < TravelComponent > linkedTravels = retrieveTravelsLinked( ticketId );
        PeriodTicket newPeriodTicket = addPeriodTicket( periodTicketMessage );
        addLinkedTravels( linkedTravels, newPeriodTicket );
        deleteTicket( ticketId );
        return newPeriodTicket;
    }

    /**
     * Retrieve all the travels that are linked to a ticket
     *
     * @param ticketId identifier of the ticket connected
     * @return the list of connected travels
     * @throws EntityNotFoundException if the specified ticket does not exist
     */
    private List < TravelComponent > retrieveTravelsLinked ( long ticketId ) throws EntityNotFoundException {
        Ticket oldTicket = getTicket( ticketId );
        return oldTicket.getLinkedTravels();
    }

    /**
     * Connects a list of travels with a ticket that is to be modified, revert all changes if the ticket becomes
     * incompatible with his previously connected travels
     *
     * @param linkedTravels travels to be linked to the ticket
     * @param ticket ticket that is to be connected with the list of travels
     * @throws IncompatibleTravelMeansException if the ticket would become incompatible with his previously connected travels
     */
    private void addLinkedTravels ( List < TravelComponent > linkedTravels, Ticket ticket )
            throws IncompatibleTravelMeansException {

        try{
            for ( TravelComponent travelComponent : linkedTravels ) {
                ticket.addLinkedTravel( travelComponent );
            }
            ticket.save();
        }catch ( IncompatibleTravelMeansException e ){
            currentUser.deleteTicket( ticket.getId() );
            currentUser.save();
            ticket.remove();
            throw new IncompatibleTravelMeansException();
        }
    }

}
