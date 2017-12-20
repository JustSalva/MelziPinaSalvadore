package it.polimi.travlendarplus.RESTful.RESTfulCalendar;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.RESTful.authenticationManager.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.authenticationManager.Secured;
import it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages.*;
import it.polimi.travlendarplus.beans.calendarManager.EventManager;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;

/**
 * This class provide all RESTful methods related to handle the user events
 * This RESTful resource will be hosted at the relative URI path "/event"
 */
@Path( "/event" )
@Secured
public class EventRESTful {

    /**
     * Enterprise Java beans that offers the logic related to events-related functionalities
     */
    @EJB
    private EventManager eventManager;

    /**
     * User that performs a request, automatically injected after his authentication
     */
    @Inject
    @AuthenticatedUser
    private User authenticatedUser;

    /**
     * This method initialize the injected event manager with the user to be handled
     * It will be executed before any request is actually performed
     */
    @PostConstruct
    public void postConstruct () {
        eventManager.setCurrentUser( authenticatedUser );
    }

    /**
     * It allows to obtain the info related to a specific event
     *
     * @param id identifier of the requested event
     * @return an HTTP 200 OK response containing the GenericEvent requested, either Event or BreakEvent,
     * or an HTTP 400 Bad Request response status code that communicate that the requested event does't exist
     */
    @Path( "{idEvent}" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getEventInformation ( @PathParam( "idEvent" ) long id ) {
        try {
            return HttpResponseBuilder.buildOkResponse( new EventDescriptionResponse(
                    eventManager.getEventInformation( id ) ) );
        } catch ( EntityNotFoundException e ) {
            try {
                return HttpResponseBuilder.buildOkResponse( new BreakEventDescriptionResponse(
                        eventManager.getBreakEventInformation( id ) ) );
            } catch ( EntityNotFoundException e1 ) {
                return HttpResponseBuilder.badRequest();
            }
        }
    }

    /**
     * It provide all the user events to be updated into the local database.
     *
     * @return a list of all the user's GenericEvents
     */
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getEvents () {
        return HttpResponseBuilder.buildOkResponse(
                new EventsListResponse( eventManager.getEvents() ) );
    }


    /**
     * It provide the user events to be updated into the local database.
     *
     * @param timestamp last update of the local database
     * @return a list of updated GenericEvents
     */
    @Path( "/updateLocalDb/{timestampLocal}" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getEventsUpdated ( @PathParam( "timestampLocal" ) long timestamp ) {
        Instant timestampLocal = Instant.ofEpochSecond( timestamp );
        return HttpResponseBuilder.buildOkResponse(
                new EventsListResponse(
                        eventManager.getEventsUpdated( timestampLocal ) ) );
    }

    /**
     * It adds an event into the user profile
     *
     * @param eventMessage eventMessage that describe the event to be added
     * @return the event info, visualized by the client or or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body )
     */
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response addEvent ( AddEventMessage eventMessage ) {
        try {
            return HttpResponseBuilder.buildOkResponse( eventManager.addEvent( eventMessage ) );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        } catch ( GMapsGeneralException e1 ){
            return HttpResponseBuilder.notAvailable();
        }
    }

    /**
     * It allows the user to modify a previously inserted event
     *
     * @param eventMessage eventMessage that describe the event fields to be modified
     * @return a 400 bad request HTTP response if the id specified does not exist or if some fields are not consistent
     * (in this case in the message body is specified which fields are wrong), an ok HTTP response otherwise
     */
    @PATCH
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyEvent ( ModifyEventMessage eventMessage ) {
        try {
            return HttpResponseBuilder.buildOkResponse( eventManager.modifyEvent( eventMessage ) );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        } catch ( GMapsGeneralException e1 ){
            return HttpResponseBuilder.notAvailable();
        }

    }

    /**
     * It allows the user to delete a previously inserted event
     *
     * @param id identifier of the event to be deleted
     * @return bad request HTTP response if the id specified does not exist, an ok HTTP response otherwise
     */
    @Path( "{idEvent}" )
    @DELETE
    public Response deleteEvent ( @PathParam( "idEvent" ) long id ) {
        try {
            eventManager.deleteEvent( id );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
        return HttpResponseBuilder.ok();
    }

    /**
     * It adds an event into the user profile
     *
     * @param eventMessage eventMessage that describe the event to be added
     * @return the event info, visualized by the client or or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body )
     */
    @Path( "/breakEvent" )
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response addBreakEvent ( AddBreakEventMessage eventMessage ) {
        try {
            return HttpResponseBuilder.buildOkResponse( eventManager.addBreakEvent( eventMessage ) );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        }
    }

    /**
     * It allows the user to modify a previously inserted event
     *
     * @param eventMessage eventMessage that describe the event fields to be modified
     * @return bad request HTTP response if the id specified does not exist or if some fields are not consistent
     * (in this case in the message body is specified which fields are wrong), an ok HTTP response otherwise
     */
    @Path( "/breakEvent" )
    @PATCH
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyBreakEvent ( ModifyBreakEventMessage eventMessage ) {
        try {
            return HttpResponseBuilder.buildOkResponse( eventManager.modifyBreakEvent( eventMessage ) );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
    }

}
