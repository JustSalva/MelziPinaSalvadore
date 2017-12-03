package it.polimi.travlendarplus.RESTful;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

// The Java class will be hosted at the URI path "/schedule"
@Path("/event")
public class EventRESTful {
    @Context
    SecurityContext securityContext;

    @Path("{idEvent}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getEventInformation(@PathParam("idEvent") int id) {
        return null;
    }

    @Path("{event}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean createEvent(@PathParam("event") JSONObject event) {
        //this function return the result of the creation.
        return false;
    }

    @Path("alternatives/{idEvent}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getAlternatives(@PathParam("idEvent") int idEvent) {
        return null;
    }

}
