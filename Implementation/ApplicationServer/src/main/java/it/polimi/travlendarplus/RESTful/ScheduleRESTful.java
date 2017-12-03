package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.security.Secured;
import org.json.JSONArray;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

// The Java class will be hosted at the URI path "/schedule"
@Path("/schedule")
@Secured

public class ScheduleRESTful {

    //the parameter day represents 00:00 of the day required
    @Path("{day}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getDailySchedule(@PathParam("day") long day) {   //TODO
        return null;
    }

    @Path("{idEvent}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray swapSchedule(@PathParam("idEvent") int id) {   //TODO
        //return daily schedule of the day changed
        return null;
    }


}
