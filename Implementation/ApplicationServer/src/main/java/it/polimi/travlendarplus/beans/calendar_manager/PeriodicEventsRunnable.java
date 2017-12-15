package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.entities.calendar.GenericEvent;

public class PeriodicEventsRunnable implements Runnable {

    private EventManager eventManager;
    private GenericEvent genericEvent;

    public PeriodicEventsRunnable( EventManager eventManager, GenericEvent genericEvent ) {
        this.eventManager = eventManager;
        this.genericEvent = genericEvent;
    }

    @Override
    public void run() {
        eventManager.propagatePeriodicEvents( genericEvent );
    }
}

