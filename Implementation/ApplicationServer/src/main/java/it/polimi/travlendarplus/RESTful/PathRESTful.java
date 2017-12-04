package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.security.Secured;
import it.polimi.travlendarplus.entities.travels.Travel;
import org.json.JSONObject;

import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

// The Java class will be hosted at the URI path "/path"
@Path("/path")
@Secured

public class PathRESTful {

    //it returns info on best path related to the specified event
    @Path("{idEvent}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getBestPathInfo(@PathParam("idEvent") int idEvent) {   //TODO
        return null;
    }

    //the user select another best path for a certain event
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void changeBestPath(Travel newBestPath) {

    }

    //it returns the information in order to draw the path
    @Path("map/{idEvent}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getPathDrawingInfo(@PathParam("idEvent") int idEvent) {   //TODO
        return null;
    }

}
