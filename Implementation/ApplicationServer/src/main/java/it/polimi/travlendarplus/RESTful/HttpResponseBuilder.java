package it.polimi.travlendarplus.RESTful;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.messages.GenericMessage;
import it.polimi.travlendarplus.messages.GenericResponseMessage;
import it.polimi.travlendarplus.messages.calendarMessages.JsonBodyResponse;

import javax.ws.rs.core.Response;

public class HttpResponseBuilder {

    public static Response unauthorized(){
        return Response.status( Response.Status.UNAUTHORIZED ).build();
    }

    public static Response forbidden(){
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public static Response badRequest(){
        return Response.status( Response.Status.BAD_REQUEST ).build();
    }

    public static Response buildOkResponse( GenericResponseMessage responseMessage){
        Gson gson = new GsonBuilder().create();
        String jsonOutput = gson.toJson( responseMessage );
        return Response.ok( jsonOutput ).build();
    }
    public static Response buildOkResponse( Object responseMessage){
        return Response.ok(  new JsonBodyResponse( responseMessage ) ).build();
    }


    public static Response buildInvalidFieldResponse( InvalidFieldException e){
        return Response.status( Response.Status.BAD_REQUEST).entity( e.getInvalidFields() ).build();
    }

    public static Response ok(){
        return Response.ok().build();
    }
}
