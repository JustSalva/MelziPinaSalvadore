package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.security.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.security.Secured;
import it.polimi.travlendarplus.beans.calendar_manager.PreferenceManager;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages.ListPreferredLocationsResponse;
import it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages.PreferredLocationMessage;
import it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages.PreferredLocationResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// The Java class will be hosted at the URI path "/preference"
@Path("/preference")
@Secured
public class PreferenceRESTful {

    @EJB
    PreferenceManager preferenceManager;

    @Inject
    @AuthenticatedUser
    User authenticatedUser;

    @PostConstruct
    public void postConstruct() {
        preferenceManager.setCurrentUser( authenticatedUser );
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPreferencesProfiles() {   //TODO
        return null;
    }

    @Path( "/{id}" )
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPreferencesProfile() {   //TODO
        return null;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPreference(TypeOfEvent toe) {   //TODO
        return null;
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifyPreference(TypeOfEvent toe) {   //TODO
        return null;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deletePreference(String typeOfEvent) {   //TODO
        return null;
    }

    //this RESTfuls manage the preferred location of the user

    @Path( "/location" )
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPreferredLocations() {
        return HttpResponseBuilder.buildOkResponse(
                new ListPreferredLocationsResponse( preferenceManager.getAllPreferredLocations() ));
    }

    @Path( "/location/{locationName}" )
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPreferredLocation(@PathParam("locationName") String locationName) {
        try {
            return HttpResponseBuilder.buildOkResponse( new PreferredLocationResponse(
                    locationName, preferenceManager.getPreferredLocation( locationName ).getAddress() ));
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
    }


    @Path( "/location" )
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPreferredLocation(PreferredLocationMessage locationMessage) {
        try {
            preferenceManager.addPreferredLocation( locationMessage );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        }
        return HttpResponseBuilder.ok();
    }

    @Path( "/location" )
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifyPreferredLocation(PreferredLocationMessage locationMessage) {
        try {
            preferenceManager.modifyPreferredLocation( locationMessage );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
        return HttpResponseBuilder.ok();
    }

    @Path( "/location" )
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deletePreferredLocation( String name ) {   //TODO
        try {
            preferenceManager.deletePreferredLocation( name );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
        return  HttpResponseBuilder.ok();
    }
}
