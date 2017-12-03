package it.polimi.travlendarplus.RESTful.security;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/example")
public class ExampleResource {

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response myUnsecuredMethod(@PathParam("id") Long id) {
        // This method is not annotated with @Secured
        // The authentication filter won't be executed before invoking this method
        return null;
    }

    @DELETE
    @Secured
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String mySecuredMethod(@PathParam("id") Long id) {
        // This method is annotated with @Secured
        // The authentication filter will be executed before invoking this method
        // The HTTP request must be performed with a valid token
        return "a";
    }
}