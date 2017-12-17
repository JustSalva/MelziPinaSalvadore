package it.polimi.travlendarplus.beans.calendarManager;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
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

@Startup
@Singleton
public class PeriodicEventsPropagator {

    @EJB
    private EventManager eventManager;

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @Schedule( hour = "23", minute = "55", second = "00" )
    public void propagatePeriodicEvents () {
        List < GenericEvent > eventsToBePropagated = getEventsToBePropagated();
        for ( GenericEvent genericEvent : eventsToBePropagated ) {
            addNextPeriodicEvent( genericEvent );
        }
    }

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

    private void createEntityManagers () {
        entityManagerFactory = Persistence.createEntityManagerFactory( "TravlendarDB" );
        entityManager = entityManagerFactory.createEntityManager();
    }

    private void closeEntityManager () {
        entityManager.close();
    }

    private void addNextPeriodicEvent ( GenericEvent event ) {
        GenericEvent genericEvent = event.nextPeriodicEvent(); //NB by default they are not scheduled
        eventManager.setCurrentUser( genericEvent.getUser() );
        eventManager.propagatePeriodicEvents( genericEvent );
        genericEvent.save();
    }

}
