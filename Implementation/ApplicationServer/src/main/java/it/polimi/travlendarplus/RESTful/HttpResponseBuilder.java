package it.polimi.travlendarplus.RESTful;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages.ErrorListResponse;
import it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages.TypeOfEventResponse;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.AlreadyScheduledException;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.tripManagerExceptions.TicketNotValidException;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provide a set of static methods that are used to build all the
 * HTTP responses sent by Travlendar+ application server
 */
public class HttpResponseBuilder {

    /**
     * Builds a response with a 401 Unauthorized status code
     *
     * @return the requested response message
     */
    public static Response unauthorized () {
        return responseBuilder( Response.Status.UNAUTHORIZED );
    }

    /**
     * Builds a response with a 403 Forbidden status code
     *
     * @return the requested response message
     */
    public static Response forbidden () {
        return responseBuilder( Response.Status.FORBIDDEN );
    }

    /**
     * Builds a response with a 400 Bad Request status code
     *
     * @return the requested response message
     */
    public static Response badRequest () {
        return responseBuilder( Response.Status.BAD_REQUEST );
    }

    /**
     * Builds a response with a 409 Conflict status code
     *
     * @return the requested response message
     */
    public static Response conflict () {
        return responseBuilder( Response.Status.CONFLICT );
    }

    /**
     * Builds a response with a 503 Service Unavailable status code
     *
     * @return the requested response message
     */
    public static Response notAvailable () {
        return responseBuilder( Response.Status.SERVICE_UNAVAILABLE );
    }

    /**
     * Builds a response with a 408 Request Timeout status code
     *
     * @return the requested response message
     */
    public static Response requestTimeout () {
        return responseBuilder( Response.Status.REQUEST_TIMEOUT );
    }

    /**
     * Builds a response with a 200 OK status code and a Json-coded message
     * in the body
     *
     * @param responseMessage message to be put into the message body
     * @return the requested response message
     */
    public static Response buildOkResponse ( Object responseMessage ) {
        Gson gson = new GsonBuilder().create();
        String jsonOutput = gson.toJson( responseMessage );
        return Response.ok( jsonOutput ).build();
    }

    /**
     * Builds a response with a 200 OK status code and a Json-coded typeOfEvent
     * message in the body
     *
     * @param responseMessage typeOfEvent to be put into the message body
     * @return the requested response message
     */
    public static Response buildTypeOfEventResponse ( TypeOfEvent responseMessage ) {
        return HttpResponseBuilder.buildOkResponse( new TypeOfEventResponse( responseMessage ) );
    }

    /**
     * Builds a response with a 200 OK status code and a Json-coded typeOfEvent
     * list message in the body
     *
     * @param responseMessage typeOfEvent list to be put into the message body
     * @return the requested response message
     */
    public static Response buildListOfTypeOfEventResponse ( List < TypeOfEvent > responseMessage ) {
        return HttpResponseBuilder.buildOkResponse(
                responseMessage.stream().map( TypeOfEventResponse::new )
                        .collect( Collectors.toCollection( ArrayList::new ) ) );
    }

    /**
     * Builds a response with a 400 Bad Request status code and a string message
     * in the body
     *
     * @param message string to be put into the message body
     * @return the requested response message
     */
    public static Response buildBadRequest ( String message ) {
        return responseBuilder( Response.Status.BAD_REQUEST, message );
    }

    /**
     * Builds a response with a 400 Bad Request status code and a list string
     * that states which fields have been mistaken in the request
     * in the message body
     *
     * @param e exception that contains the wrong fields list
     * @return the requested response message
     */
    public static Response buildInvalidFieldResponse ( InvalidFieldException e ) {
        return buildErrorsMessage( e.getInvalidFields() );
    }

    /**
     * Builds a response with a 400 Bad Request status code and a list errors
     * ( List of Strings)
     *
     * @param errors list of errors
     * @return the requested response message
     */
    private static Response buildErrorsMessage ( List < String > errors ) {
        return responseBuilder( Response.Status.BAD_REQUEST, new ErrorListResponse( errors ) );
    }

    /**
     * Builds a response with a 400 Bad Request status code and a list string
     * that states which fields have been mistaken in the request
     * in the message body
     *
     * @param e exception that contains the wrong fields list
     * @return the requested response message
     */
    public static Response buildAlreadyScheduledResponse ( AlreadyScheduledException e ) {
        return responseBuilder( Response.Status.BAD_REQUEST, e.getMessage() );
    }

    /**
     * Builds a response with a 400 Bad Request status code and a list string
     * that states why a travel component can't be connected with a ticket
     *
     * @param e exception that contains the explanations of why it can't be applied
     * @return the requested response message
     */
    public static Response buildTicketNotValidResponse ( TicketNotValidException e ) {
        return buildErrorsMessage( e.getErrors() );
    }

    /**
     * Builds a response with a 200 OK status code
     *
     * @return the requested response message
     */
    public static Response ok () {
        return Response.ok().build();
    }

    /**
     * Helper method used to build a response given its status code
     *
     * @param status status code of hte response to be built
     * @return the requested response message
     */
    private static Response responseBuilder ( Response.Status status ) {
        return Response.status( status ).build();
    }

    /**
     * Helper method used to build a response given its status code and the message
     * to be contained in the message body
     *
     * @param status  status code of hte response to be built
     * @param message object that represents the response body
     * @return the requested response message
     */
    private static Response responseBuilder ( Response.Status status, Object message ) {
        return Response.status( status ).entity( message ).build();
    }

}
