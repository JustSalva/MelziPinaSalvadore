package it.polimi.travlendarplus.beans.calendarManager;

import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

public class PeriodicEventsRunnable implements Runnable {

    @EJB
    private EventManager eventManager;
    private GenericEvent genericEvent;
    private User user;

    public PeriodicEventsRunnable ( EventManager eventManager, GenericEvent genericEvent, User user ) {
        this.eventManager = eventManager;
        this.genericEvent = genericEvent;
        this.user = user;
    }

    @PostConstruct
    public void postConstruct () {
        eventManager.setCurrentUser( user );
    }

    @Override
    public void run () {
        eventManager.propagatePeriodicEvents( genericEvent );
    }
}

