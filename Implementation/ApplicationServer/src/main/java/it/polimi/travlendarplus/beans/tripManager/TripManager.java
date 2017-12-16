package it.polimi.travlendarplus.beans.tripManager;

import it.polimi.travlendarplus.RESTful.messages.tripMessages.*;
import it.polimi.travlendarplus.beans.calendarManager.EventManager;
import it.polimi.travlendarplus.beans.calendarManager.UserManager;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.tickets.*;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class TripManager extends UserManager {

    public List < Ticket > getTickets () {
        return currentUser.getHeldTickets();
    }

    public DistanceTicket addDistanceTicket ( AddDistanceTicketMessage distanceTicketMessage )
            throws InvalidFieldException {

        checkDistanceTicketConsistency( distanceTicketMessage );
        DistanceTicket distanceTicket = createDistanceTicket( distanceTicketMessage );
        addTicketToUserAndSave( distanceTicket );
        return distanceTicket;
    }

    public DistanceTicket createDistanceTicket ( AddDistanceTicketMessage distanceTicketMessage ) {
        return new DistanceTicket( distanceTicketMessage.getCost(),
                createListOfPublicTravelMean( distanceTicketMessage ),
                distanceTicketMessage.getDistance() );
    }

    private ArrayList < PublicTravelMean > createListOfPublicTravelMean ( AddTicketMessage addTicketMessage ) {
        return addTicketMessage.getRelatedTo().stream()
                .map( message -> new PublicTravelMean( message.getName(), message.getType() ) )
                .collect( Collectors.toCollection( ArrayList::new ) );

    }

    private void addTicketToUserAndSave ( Ticket ticket ) {
        ticket.save();
        currentUser.addTicket( ticket );
        currentUser.save();
    }

    public GenericTicket addGenericTicket ( AddGenericTicketMessage genericTicketMessage )
            throws InvalidFieldException {

        checkGenericTicketConsistency( genericTicketMessage );
        GenericTicket genericTicket = createGenericTicket( genericTicketMessage );
        addTicketToUserAndSave( genericTicket );
        return genericTicket;
    }

    public GenericTicket createGenericTicket ( AddGenericTicketMessage genericTicketMessage ) {
        return new GenericTicket( genericTicketMessage.getCost(),
                createListOfPublicTravelMean( genericTicketMessage ),
                genericTicketMessage.getLineName() );
    }


    public PathTicket addPathTicket ( AddPathTicketMessage pathTicketMessage )
            throws InvalidFieldException {

        checkPathTicketConsistency( pathTicketMessage );
        PathTicket pathTicket = createPathTicket( pathTicketMessage );
        addTicketToUserAndSave( pathTicket );
        return pathTicket;
    }

    public PathTicket createPathTicket ( AddPathTicketMessage pathTicketMessage ) {
        Location departure = EventManager.findLocation( pathTicketMessage.getStartingLocation() );
        Location arrival = EventManager.findLocation( pathTicketMessage.getEndingLocation() );
        return new PathTicket( pathTicketMessage.getCost(),
                createListOfPublicTravelMean( pathTicketMessage ),
                pathTicketMessage.getLineName(), departure, arrival );
    }

    public PeriodTicket addPeriodTicket ( AddPeriodTicketMessage periodTicketMessage )
            throws InvalidFieldException {

        Ticket decorator = checkPeriodTicketConsistency( periodTicketMessage );
        PeriodTicket periodTicket = createPeriodTicket( periodTicketMessage, decorator );
        addTicketToUserAndSave( periodTicket );
        return periodTicket;
    }

    private PeriodTicket createPeriodTicket ( AddPeriodTicketMessage periodTicketMessage, Ticket decorator ) {

        return new PeriodTicket( periodTicketMessage.getCost(), createListOfPublicTravelMean( periodTicketMessage ),
                periodTicketMessage.getName(), periodTicketMessage.getStartingDate(),
                periodTicketMessage.getEndingDate(), decorator );
    }

    public void deleteTicket ( long ticketId ) throws EntityNotFoundException {
        currentUser.deleteTicket( ticketId );
        currentUser.save();
    }

    public void selectTicket ( long ticketId, long travelComponentId ) throws EntityNotFoundException {
        //TODO
    }

    public void deselectTicket ( long ticketId, long travelComponentId ) throws EntityNotFoundException {
        //TODO
    }

    public void checkDistanceTicketConsistency ( AddDistanceTicketMessage distanceTicketMessage )
            throws InvalidFieldException {
        List < String > errors = checkTicketConsistency( distanceTicketMessage );
        if ( distanceTicketMessage.getDistance() <= 0 ) {
            errors.add( " distance" );
        }
        checkErrorExistence( errors );
    }

    public void checkGenericTicketConsistency ( AddGenericTicketMessage genericTicketMessage )
            throws InvalidFieldException {
        List < String > errors = checkTicketConsistency( genericTicketMessage );
        if ( genericTicketMessage.getLineName() == null ) {
            errors.add( " line name " );
        }
        checkErrorExistence( errors );
    }

    public void checkPathTicketConsistency ( AddPathTicketMessage pathTicketMessage )
            throws InvalidFieldException {

        List < String > errors = new ArrayList <>();
        //TODO check locations?
        try {
            checkGenericTicketConsistency( pathTicketMessage );
        } catch ( InvalidFieldException e ) {
            errors.addAll( e.getInvalidFields() );
        }
        checkErrorExistence( errors );
    }

    private Ticket checkPeriodTicketConsistency ( AddPeriodTicketMessage periodTicketMessage )
            throws InvalidFieldException {

        List < String > errors = checkTicketConsistency( periodTicketMessage );
        Ticket decorator = null;
        if ( periodTicketMessage.getName() == null ) {
            errors.add( "name" );
        }
        if ( !periodTicketMessage.getStartingDate().isBefore( periodTicketMessage.getEndingDate() ) ) {
            errors.add( " minimum time must be less than ending time" );
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
            errors.add( "invalid ticket decorator" );
        }
        checkErrorExistence( errors );
        //NB this decorator cannot be null cause if it is an InvalidFieldException is thrown by the precedent instruction
        return decorator;
    }

    private List < String > checkTicketConsistency ( AddTicketMessage addTicketMessage ) {
        List < String > errors = new ArrayList <>();
        if ( addTicketMessage.getCost() < 0 ) {
            errors.add( " cost" );
        }
        for ( AddPublicTravelMeanMessage meanMessage : addTicketMessage.getRelatedTo() ) {
            errors.addAll( checkPublicTravelMeanConsistency( meanMessage ) );
        }
        return errors;
    }

    private List < String > checkPublicTravelMeanConsistency ( AddPublicTravelMeanMessage meanMessage ) {
        List < String > errors = new ArrayList <>();
        if ( meanMessage.getName() == null ) {
            errors.add( "travel mean name" );
        }
        if ( !TravelMeanEnum.isPublicTravelMean( meanMessage.getType() ) ) {
            errors.add( "travel mean type" );
        }
        return errors;
    }

    private void checkErrorExistence ( List < String > errors ) throws InvalidFieldException {
        if ( errors.size() > 0 ) {
            throw new InvalidFieldException( errors );
        }
    }

}
