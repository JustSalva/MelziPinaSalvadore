package it.polimi.travlendarplus.RESTful;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages.TypeOfEventResponse;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.AlreadyScheduledException;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpResponseBuilder {

    public static Response unauthorized () {
        return responseBuilder( Response.Status.UNAUTHORIZED );
    }

    public static Response forbidden () {
        return responseBuilder( Response.Status.FORBIDDEN );
    }

    public static Response badRequest () {
        return responseBuilder( Response.Status.BAD_REQUEST );
    }

    public static Response conflict () {
        return responseBuilder( Response.Status.CONFLICT );
    }

    public static Response notAvailable () {
        return responseBuilder( Response.Status.SERVICE_UNAVAILABLE );
    }

    public static Response requestTimeout () {
        return responseBuilder( Response.Status.REQUEST_TIMEOUT );
    }

    public static Response buildOkResponse ( Object responseMessage ) {
        Gson gson = new GsonBuilder().create();
        String jsonOutput = gson.toJson( responseMessage );
        return Response.ok( jsonOutput ).build();
    }

    public static Response buildTypeOfEventResponse ( TypeOfEvent responseMessage ) {
        return HttpResponseBuilder.buildOkResponse( new TypeOfEventResponse( responseMessage ) );
    }

    public static Response buildListOfTypeOfEventResponse ( List < TypeOfEvent > responseMessage ) {
        return HttpResponseBuilder.buildOkResponse(
                responseMessage.stream().map( TypeOfEventResponse::new )
                        .collect( Collectors.toCollection( ArrayList::new ) ) );
    }

    public static Response buildBadRequest ( String message ) {
        return responseBuilder( Response.Status.BAD_REQUEST, message );
    }

    public static Response buildInvalidFieldResponse ( InvalidFieldException e ) {
        return responseBuilder( Response.Status.BAD_REQUEST, e.getInvalidFields() );
    }

    public static Response buildAlreadyScheduledResponse ( AlreadyScheduledException e ) {
        return responseBuilder( Response.Status.BAD_REQUEST, e.getMessage() );
    }

    public static Response ok () {
        return Response.ok().build();
    }

    private static Response responseBuilder ( Response.Status status ) {
        return Response.status( status ).build();
    }

    private static Response responseBuilder ( Response.Status status, Object message ) {
        return Response.status( status ).entity( message ).build();
    }

}
