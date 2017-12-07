package it.polimi.travlendarplus.RESTful;


import it.polimi.travlendarplus.messages.GenericMessage;

import javax.ws.rs.core.Response;

public class HttpResponseBuilder {

    public static Response unhautorized(){
        return Response.status( Response.Status.UNAUTHORIZED ).build();
    }

    public static Response forbidden(){
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public static Response badRequest(){
        return Response.status( Response.Status.BAD_REQUEST ).build();
    }

    public static Response buildOkResponse( GenericMessage genericMessage){
        return Response.ok( genericMessage ).build();
    }

    public static Response ok(){
        return Response.ok().build();
    }
}
