package it.polimi.travlendarplus.RESTful.RESTfulCalendar;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.RESTful.authenticationManager.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.authenticationManager.Secured;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.AlreadyScheduledException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class provide all RESTful methods related to the schedule management
 * This RESTful resource will be hosted at the relative URI path "/schedule"
 */
@Path( "/schedule" )
@Secured
public class ScheduleRESTful {

    /**
     * Enterprise Java beans that offers the logic related to schedule management functionalities
     */
    @EJB
    ScheduleManager scheduleManager;

    /**
     * User that performs a request, automatically injected after his authentication
     */
    @Inject
    @AuthenticatedUser
    User authenticatedUser;

    /**
     * This method initialize the injected schedule manager with the user to be handled
     * It will be executed before any request is actually performed
     */
    @PostConstruct
    public void postConstruct() {
        scheduleManager.setCurrentUser( authenticatedUser );
    }


    /**
     * Allow the user to retrieve the schedule of one day
     *
     * @param day represents time 00:00 of the day required in UNIX time
     * @return the requested schedule
     */
    @Path( "{day}" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getDailySchedule( @PathParam( "day" ) long day ) {
        return HttpResponseBuilder.buildOkResponse( scheduleManager.getScheduleByDay( day ) );
    }

    /**
     * Allow the user to force in the schedule an event not scheduled
     *
     * @param id identifier of the event to be forced into the schedule
     * @return an HTTP 400 Bad Request response status code if the identifier of the event does not exist
     * or if the event is already scheduled, otherwise an HTTP 200 OK success status response code
     * with the updated schedule in the message body
     */
    @Path( "{idEvent}" )
    @PATCH
    @Produces( MediaType.APPLICATION_JSON )
    public Response swapSchedule( @PathParam( "idEvent" ) long id ) {   //TODO
        try {
            return HttpResponseBuilder.buildOkResponse( scheduleManager.swapEvents( id ) );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        } catch ( AlreadyScheduledException e ) {
            return HttpResponseBuilder.buildAlreadyScheduledResponse( e );
        }
    }


}
