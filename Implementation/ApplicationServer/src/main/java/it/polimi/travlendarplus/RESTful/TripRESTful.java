package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.authenticationManager.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.authenticationManager.Secured;
import it.polimi.travlendarplus.RESTful.messages.tripMessages.*;
import it.polimi.travlendarplus.beans.tripManager.TripManager;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.IncompatibleTravelMeansException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class provide all RESTful methods related to handle the users trips
 * This RESTful resource will be hosted at the relative URI path "/trip"
 */
@Path( "/trip" )
@Secured
public class TripRESTful {

    /**
     * Enterprise Java beans that offers the logic related to trip-related functionalities
     */
    @EJB
    private TripManager tripManager;

    /**
     * User that performs a request, automatically injected after his authentication
     */
    @Inject
    @AuthenticatedUser
    private User authenticatedUser;

    /**
     * This method initialize the injected trip manager with the user to be handled
     * It will be executed before any request is actually performed
     */
    @PostConstruct
    public void postConstruct () {
        tripManager.setCurrentUser( authenticatedUser );
    }

    /**
     * Allows the user to retrieve all his saved tickets
     *
     * @return an HTTP 200 OK success status response code and all the tickets  of the user
     * ( in the message body )
     */
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getTickets () {
        return HttpResponseBuilder.buildOkResponse( tripManager.getTickets() );
    }

    /**
     * Allows the user to add a new distance ticket
     *
     * @param distanceTicketMessage message containing the info of the distance ticket to be inserted
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body )
     */
    @Path( "/distanceTicket" )
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response addDistanceTicketMessage ( AddDistanceTicketMessage distanceTicketMessage ) {
        return addTicketMessage( distanceTicketMessage );
    }

    /**
     * Allows the user to add a new generic ticket
     *
     * @param genericTicketMessage message containing the info of the generic ticket to be inserted
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body )
     */
    @Path( "/genericTicket" )
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response addGenericTicketMessage ( AddGenericTicketMessage genericTicketMessage ) {
        return addTicketMessage( genericTicketMessage );
    }

    /**
     * Allows the user to add a new path ticket
     *
     * @param pathTicketMessage message containing the info of the path ticket to be inserted
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body )
     */
    @Path( "/pathTicket" )
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response addPathTicketMessage ( AddPathTicketMessage pathTicketMessage ) {
        return addTicketMessage( pathTicketMessage );
    }

    /**
     * Allows the user to add a new period ticket
     *
     * @param periodTicketMessage message containing the info of the period ticket to be inserted
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body )
     */
    @Path( "/periodTicket" )
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response addPeriodTicketMessage ( AddPeriodTicketMessage periodTicketMessage ) {
        return addTicketMessage( periodTicketMessage );
    }

    private Response addTicketMessage ( AddTicketMessage ticketMessage ) {
        try {
            return HttpResponseBuilder.buildOkResponse(
                    ticketMessage.addTicket( tripManager ) );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        }
    }

    /**
     * It deletes a ticket from the user personal tickets
     *
     * @param ticketId identifier of the ticket to be deleted
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code if the specified profile does not exist
     */
    @Path( "{ticketId}" )
    @DELETE
    @Consumes( MediaType.APPLICATION_JSON )
    public Response deleteTicket ( @PathParam( "ticketId" ) long ticketId ) {
        try {
            tripManager.deleteTicket( ticketId );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }
        return HttpResponseBuilder.ok();
    }


    /**
     * It allows the user to associate a ticket to a travelComponent
     *
     * @param ticketId          identifier of the ticket to be selected
     * @param travelComponentId identifier of the travelComponent to be associated with
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code if one or both the ids are correct
     * ( in the body is specified which fields are wrong )
     */
    @Path( "/selectTicket/{ticketId}/{travelComponentId}" )
    @PATCH
    public Response selectTicket ( @PathParam( "ticketId" ) long ticketId,
                                   @PathParam( "travelComponentId" ) long travelComponentId ) {

        return handleTicketSelection( ticketId, travelComponentId, true );
    }

    /**
     * It allows the user to de-associate a ticket to a travelComponent
     *
     * @param ticketId          identifier of the ticket to be deselected
     * @param travelComponentId identifier of the travelComponent to be disassociated with
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code if one or both the ids are correct
     * ( in the body is specified which fields are wrong )
     */
    @Path( "/deselectTicket/{ticketId}/{travelComponentId}" )
    @PATCH
    @Consumes( MediaType.APPLICATION_JSON )
    public Response deselectTicket ( @PathParam( "ticketId" ) long ticketId,
                                     @PathParam( "travelComponentId" ) long travelComponentId ) {

        return handleTicketSelection( ticketId, travelComponentId, false );
    }

    private Response handleTicketSelection ( long ticketId, long travelComponentId, boolean isSelection ) {

        try {
            if ( isSelection ) {
                try {
                    tripManager.selectTicket( ticketId, travelComponentId );
                }catch ( IncompatibleTravelMeansException e ) {
                    return HttpResponseBuilder.buildBadRequest( e.getMessage() );
                }
            } else {
                tripManager.deselectTicket( ticketId, travelComponentId );
            }
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.buildBadRequest( e.getMessage() );
        }
        return HttpResponseBuilder.ok();
    }

    /**
     * Allows the user to modify a distance ticket
     *
     * @param distanceTicketMessage message containing the info of the distance ticket to be modified
     * @param ticketId identifier of the ticket to be modified
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body
     *      if the body is empty the ticket does not exist)
     */
    @Path( "/distanceTicket/{ticketId}" )
    @PATCH
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyDistanceTicketMessage ( AddDistanceTicketMessage distanceTicketMessage,
                                                  @PathParam( "ticketId" ) long ticketId ) {

        return modifyTicketMessage( distanceTicketMessage, ticketId );
    }

    /**
     * Allows the user to modify a generic ticket
     *
     * @param genericTicketMessage message containing the info of the generic ticket to be modified
     * @param ticketId identifier of the ticket to be modified
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body
     *      if the body is empty the ticket does not exist)
     */
    @Path( "/genericTicket/{ticketId}" )
    @PATCH
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyGenericTicketMessage ( AddGenericTicketMessage genericTicketMessage,
                                              @PathParam( "ticketId" ) long ticketId ) {
        return modifyTicketMessage( genericTicketMessage, ticketId );
    }

    /**
     * Allows the user to modify a path ticket
     *
     * @param pathTicketMessage message containing the info of the path ticket to be modified
     * @param ticketId identifier of the ticket to be modified
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body
     *      if the body is empty the ticket does not exist)
     *
     */
    @Path( "/pathTicket/{ticketId}" )
    @PATCH
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyPathTicketMessage ( AddPathTicketMessage pathTicketMessage,
                                           @PathParam( "ticketId" ) long ticketId ) {
        return modifyTicketMessage( pathTicketMessage, ticketId );
    }

    /**
     * Allows the user to modify a period ticket
     *
     * @param periodTicketMessage message containing the info of the period ticket to be modified
     * @param ticketId identifier of the ticket to be modified
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or HTTP 400 Bad Request response status code otherwise
     * ( that means there are invalid fields, the wrong ones are specified in the message body
     *      if the body is empty the ticket does not exist)
     */
    @Path( "/periodTicket/{ticketId}" )
    @PATCH
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyPeriodTicketMessage ( AddPeriodTicketMessage periodTicketMessage,
                                             @PathParam( "ticketId" ) long ticketId  ) {
        return modifyTicketMessage( periodTicketMessage, ticketId );
    }

    private Response modifyTicketMessage ( AddTicketMessage ticketMessage, long ticketId ) {
        try {
            return HttpResponseBuilder.buildOkResponse(
                    ticketMessage.modifyTicket( tripManager, ticketId ) );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        } catch ( IncompatibleTravelMeansException e ) {
            return HttpResponseBuilder.buildBadRequest( e.getMessage() );
        }
    }

    /**
     * It allows the user to obtain the specific URLs where he can buy
     * the tickets needed for a travel
     *
     * @return an HTTP 200 OK success status response code and the requested URL
     * or HTTP 400 Bad Request response status code if the URL is not available
     */
    @Path( "/buyTicket" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response buyTicket () {
        //TODO
        return null;
    }

    /* TODO
    getNearSharingVehicles( location ): it allows the user to retrieve the location of the
    near (to him) sharing vehicles selected in his travel (GET method).
     */

}