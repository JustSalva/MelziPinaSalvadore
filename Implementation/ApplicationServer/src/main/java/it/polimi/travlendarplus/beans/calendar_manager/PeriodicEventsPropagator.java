package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.travels.Travel;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
        List < GenericEvent > periodicEvents = new ArrayList<>(  );
        periodicEvents.addAll( performEventQuery() );
        periodicEvents.addAll( performBreakEventQuery() );
        List < GenericEvent > eventsToBePropagated = new ArrayList<>();
        Instant upperbound = Instant.now().plus( 1, ChronoUnit.YEARS );

        //I select only the last (in time) event with a certain periodicity, so i can add the next periodic event
        for ( GenericEvent event : periodicEvents ){
            if ( event.getPeriodicity().getEndingDay().isAfter( upperbound )
                    && event.getStartingTime().plus( event.getPeriodicity().getDeltaDays(), ChronoUnit.DAYS )
                    .isAfter( upperbound ) && ! event.getStartingTime().isAfter( upperbound )) {
                eventsToBePropagated.add( event );
            }
        }

        return eventsToBePropagated;
    }

    private List < Event > performEventQuery(){
        createEntityManagers();

        List < Event > periodicEvents = new ArrayList<>();

        TypedQuery < Event > eventQuery = entityManager.createQuery( "" +
                        "SELECT event " +
                        "FROM EVENT event " +
                        "WHERE event.isScheduled = :TRUE AND event.periodicity <> NULL",
                Event.class);
        periodicEvents.addAll( eventQuery.getResultList() );
        closeEntityManagers();

        return periodicEvents;
    }

    private void createEntityManagers(){
        entityManagerFactory = Persistence.createEntityManagerFactory("TravlendarDB");
        entityManager = entityManagerFactory.createEntityManager();
    }

    private void closeEntityManagers(){
        entityManagerFactory = Persistence.createEntityManagerFactory("TravlendarDB");
        entityManager = entityManagerFactory.createEntityManager();
    }

    private List < BreakEvent > performBreakEventQuery(){
        createEntityManagers();

        List < BreakEvent > periodicBreakEvents = new ArrayList<>();

        TypedQuery < BreakEvent > eventQuery = entityManager.createQuery( "" +
                        "SELECT breakEvent " +
                        "FROM BREAK_EVENT breakEvent " +
                        "WHERE breakEvent.isScheduled = :TRUE AND breakEvent.periodicity <> NULL",
                BreakEvent.class);
        periodicBreakEvents.addAll( eventQuery.getResultList() );

        closeEntityManagers();

        return periodicBreakEvents;
    }


    private void addNextPeriodicEvent( GenericEvent event ){
        GenericEvent genericEvent = event.nextPeriodicEvent( ); //NB by default they are not scheduled
        //TODO check travel feasibility and add feasible travels to standard Events
        genericEvent.addInUserList( event.getUser() );
        genericEvent.save();
        event.getUser().save();
    }

}
