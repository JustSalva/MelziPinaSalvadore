package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.security.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.security.Secured;
import it.polimi.travlendarplus.beans.calendar_manager.EventManager;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.messages.calendarMessages.eventMessages.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;

/**
 *  This class provide all RESTful methods related to handle the users events
 *  This RESTful resource will be hosted at the relative URI path "/event"
 */
@Path("/event")
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
    public void postConstruct() {
        eventManager.setCurrentUser( authenticatedUser );
    }

    /**
     * It allows to obtain the info related to a specific event
     * @param id identifier of the requested event
     * @return an HTTP response containing the GenericEvent requested, either Event or BreakEvent,
     *         or an HTTP 400 Bad Request response status code that communicate that the requested event does't exist
     */
    @Path("{idEvent}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventInformation( @PathParam("idEvent") long id) {
        try {
            return HttpResponseBuilder.buildOkResponse( new EventDescriptionResponse(
                    eventManager.getEventInformation( id )) );
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
     * It provide the user events to be updated into the local database.
     * @param timestamp last update of the local database
     * @return a list of updated GenericEvents
     */
    @Path( "/updateLocalDB/{timestampLocal}" )
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventUpdated( @PathParam("timestampLocal") long timestamp){
        Instant timestampLocal = Instant.ofEpochSecond( timestamp );
        return HttpResponseBuilder.buildOkResponse(
                new EventsListResponse(
                        eventManager.getEventUpdated( timestampLocal ) ) );
    }

    /*

 performAlternativeRequests (path, unicode): it is used when the user wants to obtain
    alternative feasible paths of a selected path (GET method).*/

    /**
     * It adds an event into the user profile
     * @param eventMessage eventMessage that describe the event to be added
     * @return the event info, visualized by the client or
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEvent(AddEventMessage eventMessage) {
        //TODO periodicity not handled
        long eventId;
        try {
            eventId = eventManager.addEvent( eventMessage );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        }
        //this instruction return the result of the creation.
        return HttpResponseBuilder.buildOkResponse( new EventAddedResponse( eventId ) );
    }

    /**
     * It allows the user to modify a previously inserted event
     * @param eventMessage eventMessage that describe the event fields to be modified
     * @return bad request HTTP response if the id specified does not exist or if some fields are not consistent
     * (in this case in the message body is specified which fields are wrong), an ok HTTP response otherwise
     */
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyEvent(ModifyEventMessage eventMessage){
        //
        try {
            eventManager.modifyEvent( eventMessage );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
        return HttpResponseBuilder.ok();
    }


    @Path("alternatives/{idEvent}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlternatives(@PathParam("idEvent") int idEvent) {   //TODO
        return null;
    }

    /**
     * It allows the user to delete a previously inserted event
     * @param id identifier of the event to be deleted
     * @return bad request HTTP response if the id specified does not exist, an ok HTTP response otherwise
     */
    @Path("{idEvent}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEvent( @PathParam("idEvent") long id) {
        try {
            eventManager.deleteEvent( id );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
        return  HttpResponseBuilder.ok();
    }

}
