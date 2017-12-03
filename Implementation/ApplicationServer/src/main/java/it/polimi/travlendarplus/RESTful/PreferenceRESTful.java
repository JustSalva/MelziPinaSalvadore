package it.polimi.travlendarplus.RESTful;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

// The Java class will be hosted at the URI path "/preference"
@Path("/preference")
public class PreferenceRESTful {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getAllPreferences() {
        return null;
    }

    @Path("{idToe}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getTypeOfEvent(@PathParam("idToe") int id) {
        return null;
    }


    @Path("{toe}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public boolean createTypeOfEvent(@PathParam("toe")JSONObject toe) {
        return false;
    }

    //this RESTfuls manage the preferred location of the user

    @Path("location")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getAllPreferredLocations() {
        return null;
    }

    @Path("location/{idLoc}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject getPreferredLocation(@PathParam("idLoc") int idLoc) {
        return null;
    }


    @Path("location/{loc}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public boolean addPreferredLocation(@PathParam("loc")JSONObject loc) {
        return false;
    }
}
