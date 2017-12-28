package it.polimi.travlendarplus.beans.calendarManager;

import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Stateful Enterprise Java Bean is to be used to propagate the periodic events within an year
 */
@Stateful
public class PeriodicEventsExecutor {

    @EJB
    private EventManager eventManager;

    /**
     * Asynchronous method that will start a thread and invoke an event propagation
     *
     * @param eventId identifier of the event to be propagated
     * @param userId  identifier of the event owner
     */
    @Asynchronous
    public void startEventPropagatorThread ( long eventId, String userId ) {
        try {
            User user = User.load( userId );
            eventManager.setCurrentUser( user );
            GenericEvent genericEvent = eventManager.getEventInformation( eventId );
            eventManager.propagatePeriodicEvents( genericEvent );
        } catch ( EntityNotFoundException | GMapsGeneralException e ) {
            /* if it is an EntityNotFoundException no action here the user certainly
            exists since we are propagating one of his events,
            the consideration is valid also for the generic event*/
            /* if it is an GMapsGeneralException the propagation will simply stop
             * and it will restarts when the PeriodicEventPropagator singleton
             * will check the periodic events to be propagated ( it runs periodically )*/
            Logger.getLogger( PeriodicEventsExecutor.class.getName() )
                    .log( Level.SEVERE, e.getMessage(), e );
        }
    }

}
