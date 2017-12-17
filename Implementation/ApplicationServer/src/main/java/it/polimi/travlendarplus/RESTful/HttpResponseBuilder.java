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
        return Response.status( Response.Status.UNAUTHORIZED ).build();
    }

    public static Response forbidden () {
        return Response.status( Response.Status.FORBIDDEN ).build();
    }

    public static Response badRequest () {
        return Response.status( Response.Status.BAD_REQUEST ).build();
    }

    public static Response conflict () {
        return Response.status( Response.Status.CONFLICT ).build();
    }

    public static Response notAvaiable () {
        return Response.status( Response.Status.SERVICE_UNAVAILABLE ).build();
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
        return Response.status( Response.Status.BAD_REQUEST ).entity( message ).build();
    }

    public static Response buildInvalidFieldResponse ( InvalidFieldException e ) {
        return Response.status( Response.Status.BAD_REQUEST ).entity( e.getInvalidFields() ).build();
    }

    public static Response buildAlreadyScheduledResponse ( AlreadyScheduledException e ) {
        return Response.status( Response.Status.BAD_REQUEST ).entity( e.getMessage() ).build();
    }


    public static Response ok () {
        return Response.ok().build();
    }
}
