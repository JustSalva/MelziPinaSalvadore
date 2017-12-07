package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.security.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.security.Secured;
import it.polimi.travlendarplus.beans.calendar_manager.EventManager;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.messages.calendarMessages.*;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import org.json.JSONArray;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;

// The Java class will be hosted at the URI path "/event"
@Path("/event")
@Secured
public class EventRESTful {

    @EJB
    EventManager eventManager;

    @Inject
    @AuthenticatedUser
    User authenticatedUser;

    @PostConstruct
    public void postConstruct() {
        eventManager.setCurrentUser( authenticatedUser );
    }

    /**
     * It allows to obtain the info related to a specific event
     * @param id identifier of the requested event
     * @return an HTTP response containing the GenericEvent requested, either Event or BreakEvent,
     *         or an HTTP that communicate that the requested event does't exist
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
     * @param timestampLocal last update of the local database
     * @return a list of updated GenericEvents
     */
    @Path( "/updateLocalDB" )
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventUpdated( Instant timestampLocal){
        return HttpResponseBuilder.buildOkResponse(
                new UpdatedEventsResponse(
                        eventManager.getEventUpdated( timestampLocal ) ) );
    }

    /*

 performAlternativeRequests (path, unicode): it is used when the user wants to obtain
    alternative feasible paths of a selected path (GET method).*/

    /**
     * It adds an event into the user profile
     * @param eventMessage eventMessage that describe the event to be added
     * @return the event info, visualized by the client
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
