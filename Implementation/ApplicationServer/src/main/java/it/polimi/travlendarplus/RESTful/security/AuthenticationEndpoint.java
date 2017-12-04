package it.polimi.travlendarplus.RESTful.security;

import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.UserNotRegisteredException;
import it.polimi.travlendarplus.messages.Credentials;
import it.polimi.travlendarplus.exceptions.InvalidCredentialsException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authentication")
public class AuthenticationEndpoint {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(Credentials credentials) {

        User user;

        try {
            // Authenticate the user using the credentials provided
            user = authenticate( credentials.getEmail(), credentials.getPassword() );

        } catch (InvalidCredentialsException e1) {
            //TODO to be tested
            return Response.status(Response.Status.FORBIDDEN).build();

        } catch (UserNotRegisteredException e2) {

            return Response.status(Response.Status.NOT_FOUND).build();
        }


        // Issue a token for the user
        String token = issueToken( user, credentials.getIdDevice() );

        // Return the token on the response
        return Response.ok(token).build();
    }

    /**
     * Find out if exist a user with the specified credentials
     * @param email username of the user
     * @param password password of the user
     * @throws InvalidCredentialsException if not exist an user with the specified credentials
     * @throws UserNotRegisteredException if the user credential specified are not registered in the system
     */
    private User authenticate(String email, String password) throws InvalidCredentialsException, UserNotRegisteredException {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid or
        User userToBeAuthenticated = User.load(email);

        if ( userToBeAuthenticated == null ) {
            throw new UserNotRegisteredException();
        }else if ( ! userToBeAuthenticated.getPassword().equals( password ) ){
            throw new InvalidCredentialsException();
        }

        return userToBeAuthenticated;
    }

    private String issueToken( User user, String idDevice) {
        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be associated to a user
        // Return the issued token
        user.addUserDevice( idDevice );
        return user.getUserDevice( idDevice ).getUnivocalCode() ;
    }
}