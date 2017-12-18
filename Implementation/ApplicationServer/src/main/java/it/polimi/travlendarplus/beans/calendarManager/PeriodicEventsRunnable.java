package it.polimi.travlendarplus.beans.calendarManager;

import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;

/**
 * Runnable used to propagate a periodic event within the year asynchronously
 */
public class PeriodicEventsRunnable implements Runnable {

    private EventManager eventManager;
    private GenericEvent genericEvent;
    private User user;

    public PeriodicEventsRunnable ( EventManager eventManager, GenericEvent genericEvent, User user ) {
        this.eventManager = eventManager;
        this.genericEvent = genericEvent;
        this.user = user;
    }

    @Override
    public void run () {
        eventManager.propagatePeriodicEvents( genericEvent );
    }
}

