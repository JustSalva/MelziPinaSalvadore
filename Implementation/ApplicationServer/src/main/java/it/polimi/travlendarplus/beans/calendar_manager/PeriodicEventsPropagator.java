package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.calendar.Period;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Startup
@Singleton
public class PeriodicEventsPropagator {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    //TODO uncomment when periodic events propagation is done
    //@Schedule(hour = "23", minute = "55", second = "00")
    public void propagatePeriodicEvents() {
        List < GenericEvent > eventsToBePropagated = getEventsToBePropagated();
        for ( GenericEvent genericEvent : eventsToBePropagated ){
            addNextPeriodicEvent( genericEvent );
        }
    }

    private List < GenericEvent > getEventsToBePropagated(){

        List < Long > genericEventIds = performPeriodicityQuery();

        List < GenericEvent > periodicEvents = new ArrayList<>( performEventQuery( genericEventIds ) );
        List < GenericEvent > eventsToBePropagated = new ArrayList<>();

        Instant upperbound = Instant.now().plus( 1, ChronoUnit.YEARS );

        //I keep only the events that needs to be propagated
        for ( GenericEvent event : periodicEvents ){
            if ( event.getPeriodicity().getEndingDay().isAfter( upperbound )
                    && event.getStartingTime().plus( event.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS )
                    .isAfter( upperbound ) && ! event.getStartingTime().isAfter( upperbound )) {
                eventsToBePropagated.add( event );
            }
        }

        return eventsToBePropagated;
    }
    private List < Long > performPeriodicityQuery (){
        createEntityManagers();

        List < Long > periodicEvents = new ArrayList<>();

        TypedQuery < Long > eventQuery = entityManager.createQuery( "" +
                        "SELECT period.lastPropagatedEvent " +
                        "FROM PERIOD period " +
                        "WHERE period.lastPropagatedEvent <> NULL",
                Long.class);
        periodicEvents= new ArrayList<>( eventQuery.getResultList() );
        closeEntityManager();

        return periodicEvents;
    }
    private List < GenericEvent > performEventQuery( List< Long > eventIds){

        List < GenericEvent > periodicEvents = new ArrayList<>(  );
        GenericEvent genericEvent;

        for ( Long id: eventIds ){
            try {
                genericEvent = Event.load( id );
                periodicEvents.add( genericEvent );
            } catch ( EntityNotFoundException e ) {
                try {
                    genericEvent = BreakEvent.load( id );
                    periodicEvents.add( genericEvent );
                } catch ( EntityNotFoundException e1 ) {
                    // if the pointer does not exist it will be not propagated, exceptional case
                    //TODO notify admin?
                }
            }
        }
        return periodicEvents;
    }

    private void createEntityManagers(){
        entityManagerFactory = Persistence.createEntityManagerFactory("TravlendarDB");
        entityManager = entityManagerFactory.createEntityManager();
    }

    private void closeEntityManager(){
        entityManager.close();
    }

    private void addNextPeriodicEvent( GenericEvent event ){
        GenericEvent genericEvent = event.nextPeriodicEvent( ); //NB by default they are not scheduled
        //TODO check travel feasibility and add feasible travels to standard Events
        genericEvent.save();
        genericEvent.addInUserList( event.getUser() );
        event.getUser().save();
    }

}
