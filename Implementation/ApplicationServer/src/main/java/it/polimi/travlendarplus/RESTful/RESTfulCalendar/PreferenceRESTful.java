package it.polimi.travlendarplus.RESTful.RESTfulCalendar;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.RESTful.authenticationManager.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.authenticationManager.Secured;
import it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages.AddTypeOfEventMessage;
import it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages.ModifyTypeOfEventMessage;
import it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages.PreferredLocationMessage;
import it.polimi.travlendarplus.beans.calendarManager.PreferenceManager;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class provide all RESTful methods related to handle the users preferences
 * This RESTful resource will be hosted at the relative URI path "/preference"
 */
@Path( "/preference" )
@Secured
public class PreferenceRESTful {

    /**
     * Enterprise Java beans that offers the logic related to preference-related functionalities
     */
    @EJB
    private PreferenceManager preferenceManager;

    /**
     * User that performs a request, automatically injected after his authentication
     */
    @Inject
    @AuthenticatedUser
    private User authenticatedUser;

    /**
     * This method initialize the injected preference manager with the user to be handled
     * It will be executed before any request is actually performed
     */
    @PostConstruct
    public void postConstruct () {
        preferenceManager.setCurrentUser( authenticatedUser );
    }


    /**
     * Allows the user to retrieve all preferences profile
     *
     * @return an HTTP 200 OK success status response code and all the preference profiles of the user
     * ( in the message body )
     */
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getPreferencesProfiles () {
        return HttpResponseBuilder.buildListOfTypeOfEventResponse( preferenceManager.getPreferencesProfiles() );
    }

    /**
     * Allows the user to retrieve a specific preference profile
     *
     * @param id identifier of the requested profile
     * @return an HTTP 200 OK success status response code and the requested profile ( in the message body )
     * if present, an HTTP 400 Bad Request response status code otherwise
     */
    @Path( "/{id}" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getPreferencesProfile ( @PathParam( "id" ) long id ) {
        try {
            return HttpResponseBuilder.buildTypeOfEventResponse( preferenceManager.getPreferencesProfile( id ) );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
    }

    /**
     * Allows the user to add a new preference profile
     *
     * @param typeOfEventMessage message containing the info of the new profile
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body )
     */
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response addPreference ( AddTypeOfEventMessage typeOfEventMessage ) {
        try {
            return HttpResponseBuilder.buildTypeOfEventResponse( preferenceManager.addTypeOfEvent( typeOfEventMessage ) );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        }
    }

    /**
     * Allows the user to modify one of his preference profiles
     *
     * @param typeOfEventMessage message containing the info of the profile to be modified
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise ( in the body is specified which fields are wrong,
     * if the body is empty the preferred location to be modified does not exist )
     */
    @PATCH
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyPreference ( ModifyTypeOfEventMessage typeOfEventMessage ) {
        try {
            return HttpResponseBuilder.buildTypeOfEventResponse( preferenceManager.modifyTypeOfEvent( typeOfEventMessage ) );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
    }

    /**
     * It allows the user to delete a previously inserted preference profile
     *
     * @param id identifier of the profile to be deleted
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code if the specified profile does not exist
     */
    @Path( "{idPreference}" )
    @DELETE
    @Consumes( MediaType.APPLICATION_JSON )
    public Response deletePreference ( @PathParam( "idPreference" ) long id ) {
        try {
            preferenceManager.deleteTypeOfEvent( id );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
        return HttpResponseBuilder.ok();
    }

    //this RESTfuls manage the preferred location of the user

    /**
     * Allows the user to retrieve all his preferred locations
     *
     * @return all the preferred location of the authenticated user
     */
    @Path( "/location" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getAllPreferredLocations () {
        return HttpResponseBuilder.buildOkResponse( preferenceManager.getAllPreferredLocations() );
    }

    /**
     * It allows to obtain the info related to a specific preferred location
     *
     * @param locationName name of the location, set by the user
     * @return an HTTP 200 OK success status response code and the requested location ( in the message body )
     * if present, an HTTP 400 Bad Request response status code otherwise
     */
    @Path( "/location/{locationName}" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getPreferredLocation ( @PathParam( "locationName" ) String locationName ) {
        try {
            return HttpResponseBuilder.buildOkResponse( preferenceManager.getPreferredLocation( locationName ) );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
    }


    /**
     * It allows the user to add a specific preferred location
     *
     * @param locationMessage message containing the location info
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body )
     */
    @Path( "/location" )
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    public Response addPreferredLocation ( PreferredLocationMessage locationMessage ) {
        try {
            preferenceManager.addPreferredLocation( locationMessage );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        }
        return HttpResponseBuilder.ok();
    }

    /**
     * It allows the user to modify a previously inserted preferred location
     *
     * @param locationMessage describe the fields to be modified
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise ( in the body is specified which fields are wrong,
     * if the body is empty the preferred location to be modified does not exist )
     */
    @Path( "/location" )
    @PATCH
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyPreferredLocation ( PreferredLocationMessage locationMessage ) {
        try {
            preferenceManager.modifyPreferredLocation( locationMessage );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
        return HttpResponseBuilder.ok();
    }

    /**
     * It allows the user to delete a previously inserted preferred location
     *
     * @param name identifier of the preferred location to be deleted
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or an HTTP 400 Bad Request response status code if the preferred location specified does not exist
     */
    @Path( "/location/{name}" )
    @DELETE
    @Consumes( MediaType.APPLICATION_JSON )
    public Response deletePreferredLocation ( @PathParam( "name" ) String name ) {
        try {
            preferenceManager.deletePreferredLocation( name );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
        return HttpResponseBuilder.ok();
    }
}
