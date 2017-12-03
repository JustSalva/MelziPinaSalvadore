package it.polimi.travlendarplus.RESTful;

import org.json.JSONArray;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

// The Java class will be hosted at the URI path "/schedule"
@Path("/schedule")

public class ScheduleRESTful {
    @Context
    SecurityContext securityContext;

    //the parameter day represents 00:00 of the day required
    @Path("{day}")
    @GET
    //@Produces(MediaType.APPLICATION_JSON)
    @Produces("text/plain")
    public /*JSONArray*/ String getDailySchedule(@PathParam("day") long day) {
        //return null;
        return "ok!";
    }

    @Path("{idEvent}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray swapSchedule(@PathParam("idEvent") int id) {
        //return daily schedule of the day changed
        return null;
    }


}
