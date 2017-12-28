package it.polimi.travlendarplus.RESTful.RESTfulCalendar;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.RESTful.authenticationManager.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.authenticationManager.Secured;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.entities.User;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    private ScheduleManager scheduleManager;

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
    public void postConstruct () {
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
    public Response getDailySchedule ( @PathParam( "day" ) long day ) {
        return HttpResponseBuilder.buildOkResponse( scheduleManager.getScheduleByDay( day ) );
    }

}
