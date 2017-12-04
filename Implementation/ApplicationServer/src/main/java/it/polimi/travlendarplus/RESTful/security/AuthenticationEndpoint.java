package it.polimi.travlendarplus.RESTful.security;

import it.polimi.travlendarplus.messages.Credentials;
import it.polimi.travlendarplus.travlendarPlusExceptions.InvalidCredentialsException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authentication")
public class AuthenticationEndpoint {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(Credentials credentials) {
        try {

            // Authenticate the user using the credentials provided
            authenticate( credentials.getUsername(), credentials.getPassword() );

            // Issue a token for the user
            String token = issueToken( credentials.getUsername() );

            // Return the token on the response
            return Response.ok(token).build();

        } catch (InvalidCredentialsException e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private void authenticate(String username, String password) throws InvalidCredentialsException {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid

        //TODO
    }

    private String issueToken(String username) {
        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be associated to a user
        // Return the issued token
        return "token";

        //TODO
    }
}