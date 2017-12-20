package it.polimi.travlendarplus.beans.calendarManager;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Singleton Enterprise Java Bean manage the propagation of periodic events
 * in the time, it provide a thread pool to propagate the events for the first year
 * ( due to heavy work ) and every day checks if an event is propagated within the year,
 * if not he propagate it.
 */
@Startup
@Singleton
public class PeriodicEventsPropagator {

    /**
     * Event managers, used by this singleton to propagate the periodic events
     */
    @EJB
    private EventManager eventManager;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Every day at midnight this method is invoked to propagate periodic events
     */
    @Schedule( hour = "23", minute = "55", second = "00" )
    public void propagatePeriodicEvents () throws GMapsGeneralException {
        List < GenericEvent > eventsToBePropagated = getEventsToBePropagated();
        for ( GenericEvent genericEvent : eventsToBePropagated ) {
            addNextPeriodicEvent( genericEvent );
        }
    }

    /**
     * Retrieves from the database the events to be propagated in time
     *
     * @return a list of generic events to be propagated
     */
    private List < GenericEvent > getEventsToBePropagated () {

        List < Long > genericEventIds = performPeriodicityQuery();

        List < GenericEvent > periodicEvents = new ArrayList <>( performEventQuery( genericEventIds ) );
        List < GenericEvent > eventsToBePropagated = new ArrayList <>();

        Instant upperbound = Instant.now().plus( 365, ChronoUnit.DAYS );

        //I keep only the events that needs to be propagated
        for ( GenericEvent event : periodicEvents ) {
            if ( event.getPeriodicity().getEndingDay().isAfter( upperbound )
                    && event.getStartingTime().plus( event.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS )
                    .isAfter( upperbound ) && !event.getStartingTime().isAfter( upperbound ) ) {
                eventsToBePropagated.add( event );
            }
        }

        return eventsToBePropagated;
    }

    /**
     * Performs a query on database to extract all Period classes,
     * which represent the boundaries and the slack of the propagation of the events
     *
     * @return all the period classes to be propagated
     * @see it.polimi.travlendarplus.entities.calendar.Period
     */
    private List < Long > performPeriodicityQuery () {
        createEntityManagers();

        List < Long > periodicEvents = new ArrayList <>();

        TypedQuery < Long > eventQuery = entityManager.createQuery( "" +
                        "SELECT period.lastPropagatedEvent " +
                        "FROM PERIOD period " +
                        "WHERE period.lastPropagatedEvent <> NULL",
                Long.class );
        periodicEvents = new ArrayList <>( eventQuery.getResultList() );
        closeEntityManager();

        return periodicEvents;
    }

    /**
     * Performs a query on database to extract Generic event classes
     * ( both standard Events and BreakEvents )
     *
     * @param eventIds identifiers of the events to be extracted
     * @return a list containing the extracted events
     */
    private List < GenericEvent > performEventQuery ( List < Long > eventIds ) {

        List < GenericEvent > periodicEvents = new ArrayList <>();
        GenericEvent genericEvent;

        for ( Long id : eventIds ) {
            try {
                genericEvent = Event.load( id );
                periodicEvents.add( genericEvent );
            } catch ( EntityNotFoundException e ) {
                try {
                    genericEvent = BreakEvent.load( id );
                    periodicEvents.add( genericEvent );
                } catch ( EntityNotFoundException e1 ) {
                    // if the pointer does not exist it will be not propagated, exceptional case
                    Logger.getLogger( PeriodicEventsPropagator.class.getName() )
                            .log( Level.SEVERE, "failure during event propagation - entity not found", e );
                }
            }
        }
        return periodicEvents;
    }

    /**
     * Instantiate the entity manager that will handle the transactions
     */
    private void createEntityManagers () {
        entityManagerFactory = Persistence.createEntityManagerFactory( "TravlendarDB" );
        entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * Closes the entity manager after a Database query ends
     */
    private void closeEntityManager () {
        entityManager.close();
    }

    /**
     * Insert a specified event into a user's calendar
     *
     * @param event event to be added into the user's calendar
     */
    private void addNextPeriodicEvent ( GenericEvent event ) throws GMapsGeneralException {
        GenericEvent genericEvent = event.nextPeriodicEvent(); //NB by default they are not scheduled
        eventManager.setCurrentUser( genericEvent.getUser() );
        eventManager.propagatePeriodicEvents( genericEvent );
        genericEvent.save();
    }

}
