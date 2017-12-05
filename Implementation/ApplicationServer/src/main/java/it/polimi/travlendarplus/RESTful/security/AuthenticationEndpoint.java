package it.polimi.travlendarplus.RESTful.security;

import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.UserDevice;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.UserNotRegisteredException;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.InvalidCredentialsException;
import it.polimi.travlendarplus.messages.authenticationMessages.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authentication")
public class AuthenticationEndpoint {
    //TODO encryption of the messages!!!
    /*
    register(registrationForm, IdDevice): it is used when a user registers himself into the system.
To do so the encrypted registration form (email, name, surname, password and captcha)
is sent to the Application Server , which returns an univocal code related to the device
(POST method);
 submitLogin(mail, password, IdDevice): it is used when a user has to log into the system,
it returns an univocal code related to the device (POST method);
 editProle(registrationForm): it is used when a user wants to modify his prole, to do so it
sends the same info contained in the registration form (UPDATE method);
 requestPublicKey(IDdevice): it is used when an user is about to log into the system and so
it must obtain a public key to encrypt his password (GET method);
 deleteProle(mail, password, IdDevice): it allows the user to remove his prole from
Travlender+ (DELETE method);
 askNewCredentials(mail): it allows the user to request a new password, that will be sent to
his email (PATCH method).
     */
    @Path("/register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register( RegistrationForm registrationForm){
        User userToBeRegistered = User.load( registrationForm.getEmail() );
        if ( userToBeRegistered != null ) {
            return Response.status( Response.Status.UNAUTHORIZED ).build();
        }
        //TODO checks on fields consistency?
        userToBeRegistered = new User( registrationForm.getEmail(), registrationForm.getName(),
                registrationForm.getSurname(), registrationForm.getPassword() );

        String token = issueToken( userToBeRegistered, registrationForm.getIdDevice() );
        // Return the token on the response
        return buildResponseToken( token );
    }

    @Path("/login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitLogin(Credentials credentials) {

        User user;
        try {
            // Authenticate the user using the credentials provided
            user = authenticate( credentials.getEmail(), credentials.getPassword() );
        } catch (InvalidCredentialsException e1) {
            //TODO to be tested
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (UserNotRegisteredException e2) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        // Issue a token for the user
        String token = issueToken( user, credentials.getIdDevice() );
        // Return the token on the response
        return buildResponseToken( token );
    }

    /**
     * Find out if exist a user with the specified credentials
     * @param email username of the user
     * @param password password of the user
     * @throws InvalidCredentialsException if not exist an user with the specified credentials
     * @throws UserNotRegisteredException if the user credential specified are not registered in the system
     */
    private User authenticate(String email, String password) throws InvalidCredentialsException, UserNotRegisteredException {

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
        //if a token already exists it replaces it
        UserDevice userDevice = UserDevice.load( idDevice );
        
        if( userDevice != null){
            userDevice.remove();
        }

        user.addUserDevice( idDevice );
        user.save();
        return user.getUserDevice( idDevice ).getUnivocalCode() ;

    }

    private Response buildResponseToken(String token){
        return Response.ok(new TokenResponse( token ) ).build();
    }
}