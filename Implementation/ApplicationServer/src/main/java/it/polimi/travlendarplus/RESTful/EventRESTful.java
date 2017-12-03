package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.security.Secured;
import it.polimi.travlendarplus.entities.calendar.Event;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

// The Java class will be hosted at the URI path "/schedule"
@Path("/event")
@Secured

public class EventRESTful {

    @Path("{idEvent}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getEventInformation(@PathParam("idEvent") int id) {    //TODO
        return null;
    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean createEvent(Event event) {   //TODO
        //this function return the result of the creation.
        return false;
    }

    @Path("alternatives/{idEvent}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getAlternatives(@PathParam("idEvent") int idEvent) {   //TODO
        return null;
    }

}
