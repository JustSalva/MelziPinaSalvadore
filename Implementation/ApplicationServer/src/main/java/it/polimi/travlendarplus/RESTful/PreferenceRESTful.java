package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.security.Secured;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

// The Java class will be hosted at the URI path "/preference"
@Path("/preference")
@Secured

public class PreferenceRESTful {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getAllPreferences() {   //TODO
        return null;
    }

    @Path("{idToe}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getTypeOfEvent(@PathParam("idToe") int id) {   //TODO
        return null;
    }


    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean createTypeOfEvent(TypeOfEvent toe) {   //TODO
        return false;
    }

    //this RESTfuls manage the preferred location of the user

    @Path("location")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getAllPreferredLocations() {   //TODO
        return null;
    }

    @Path("location/{idLoc}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getPreferredLocation(@PathParam("idLoc") int idLoc) {   //TODO
        return null;
    }


    @Path("location/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean addPreferredLocation(Location loc) {   //TODO
        return false;
    }
}
