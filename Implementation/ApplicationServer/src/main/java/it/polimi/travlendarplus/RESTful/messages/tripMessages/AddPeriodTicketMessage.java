package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.beans.tripManager.TripManager;
import it.polimi.travlendarplus.entities.tickets.DistanceTicket;
import it.polimi.travlendarplus.entities.tickets.GenericTicket;
import it.polimi.travlendarplus.entities.tickets.PathTicket;
import it.polimi.travlendarplus.entities.tickets.Ticket;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.IncompatibleTravelMeansException;

import java.time.Instant;
import java.util.List;

public class AddPeriodTicketMessage extends AddTicketMessage {

    private static final long serialVersionUID = 4030989447613137135L;

    private String name;
    private Instant startingDate;
    private Instant endingDate;
    private AddDistanceTicketMessage distanceDecorator;
    private AddGenericTicketMessage genericDecorator;
    private AddPathTicketMessage pathDecorator;

    public AddPeriodTicketMessage () {
    }

    public AddPeriodTicketMessage ( float cost, List < AddPublicTravelMeanMessage > relatedTo,
                                    String name, Instant startingDate, Instant endingDate ) {
        super( cost, relatedTo );
        this.name = name;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public Instant getStartingDate () {
        return startingDate;
    }

    public void setStartingDate ( Instant startingDate ) {
        this.startingDate = startingDate;
    }

    public Instant getEndingDate () {
        return endingDate;
    }

    public void setEndingDate ( Instant endingDate ) {
        this.endingDate = endingDate;
    }

    public AddDistanceTicketMessage getDistanceDecorator () {
        return distanceDecorator;
    }

    public AddGenericTicketMessage getGenericDecorator () {
        return genericDecorator;
    }

    public AddPathTicketMessage getPathDecorator () {
        return pathDecorator;
    }

    public boolean isDistanceDecorator () {
        return ( distanceDecorator != null );
    }

    public void setDistanceDecorator ( AddDistanceTicketMessage distanceDecorator ) {
        this.distanceDecorator = distanceDecorator;
    }

    public boolean isGenericDecorator () {
        return ( genericDecorator != null );
    }

    public void setGenericDecorator ( AddGenericTicketMessage genericDecorator ) {
        this.genericDecorator = genericDecorator;
    }

    public boolean isPathDecorator () {
        return ( pathDecorator != null );
    }

    public void setPathDecorator ( AddPathTicketMessage pathDecorator ) {
        this.pathDecorator = pathDecorator;
    }

    /**
     * The ticket is consistent if only one of the decorators is present
     *
     * @return true if the ticket is consistent, false otherwise
     */
    public boolean isConsistent () {
        boolean xorBetweenFirstAndSecond = ( isDistanceDecorator() && !isGenericDecorator() ) ||
                ( !isDistanceDecorator() && isGenericDecorator() );
        //and now XOR between the first xor and the third value and case in which all three are = 1
        boolean secondXor = ( isPathDecorator() && !xorBetweenFirstAndSecond ) ||
                ( !isPathDecorator() && xorBetweenFirstAndSecond );
        boolean allThreeTrue = isDistanceDecorator() && isGenericDecorator() && isPathDecorator();

        return secondXor && !allThreeTrue;
    }

    /**
     * Provide the distance ticket contained inside the message
     *
     * @param tripManager manager that will handle the ticket creation
     * @return the requested distance ticket
     * @throws InvalidFieldException if some fields of the message are wrong
     *                               ( which one is specified inside the exception)
     */
    public DistanceTicket retrieveDistanceTicket ( TripManager tripManager )
            throws InvalidFieldException {

        tripManager.checkDistanceTicketConsistency( distanceDecorator );
        return tripManager.createDistanceTicket( distanceDecorator );
    }

    /**
     * Provide the generic ticket contained inside the message
     *
     * @param tripManager manager that will handle the ticket creation
     * @return the requested generic ticket
     * @throws InvalidFieldException if some fields of the message are wrong
     *                               ( which one is specified inside the exception)
     */
    public GenericTicket retrieveGenericTicket ( TripManager tripManager )
            throws InvalidFieldException {

        tripManager.checkGenericTicketConsistency( genericDecorator );
        return tripManager.createGenericTicket( genericDecorator );
    }

    /**
     * Provide the path ticket contained inside the message
     *
     * @param tripManager manager that will handle the ticket creation
     * @return the requested path ticket
     * @throws InvalidFieldException if some fields of the message are wrong
     *                               ( which one is specified inside the exception)
     */
    public PathTicket retrievePathTicket ( TripManager tripManager )
            throws InvalidFieldException {

        tripManager.checkPathTicketConsistency( pathDecorator );
        return tripManager.createPathTicket( pathDecorator );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ticket addTicket ( TripManager tripManager ) throws InvalidFieldException {
        return tripManager.addPeriodTicket( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ticket modifyTicket ( TripManager tripManager, long ticketId )
            throws InvalidFieldException, EntityNotFoundException, IncompatibleTravelMeansException {

        return tripManager.modifyPeriodTicket( this, ticketId );
    }
}
