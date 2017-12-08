package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.security.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.security.Secured;
import it.polimi.travlendarplus.beans.calendar_manager.ScheduleManager;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import org.json.JSONArray;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Instant;

// The Java class will be hosted at the URI path "/schedule"
@Path("/schedule")
@Secured

public class ScheduleRESTful {

    @EJB
    ScheduleManager scheduleManager;

    @Inject
    @AuthenticatedUser
    User authenticatedUser;

    public void postConstruct() {
        scheduleManager.setCurrentUser( authenticatedUser );
    }

    //the parameter day represents 00:00 of the day required
    @Path("{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getDailySchedule(@PathParam("day") long day) {
        scheduleManager.setCurrentUser(authenticatedUser);
        return new JSONArray(scheduleManager.getScheduleByDay(day));
    }

    @Path("{idEvent}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray swapSchedule(@PathParam("idEvent") long id) {   //TODO
        scheduleManager.setCurrentUser(authenticatedUser);
        try {
            return new JSONArray(scheduleManager.swapEvents(Event.load(id)));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return new JSONArray(scheduleManager.getScheduleByDay(Instant.now().getEpochSecond()));
        }
    }


}
