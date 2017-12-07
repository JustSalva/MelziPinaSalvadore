package it.polimi.travlendarplus.RESTful.security;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.email.EmailSender;
import it.polimi.travlendarplus.entities.RSAEncryption;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.UserDevice;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.UserNotRegisteredException;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.InvalidCredentialsException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.messages.authenticationMessages.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.PublicKey;

@Path("/")
public class AuthenticationEndpoint {
    //TODO encryption of the messages!!!

    @Path("/register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register( RegistrationForm registrationForm){
        User userToBeRegistered;
        try {
            userToBeRegistered = loadUser( registrationForm.getEmail() );
        } catch ( UserNotRegisteredException e ) {
            //TODO checks on fields consistency?
            userToBeRegistered = new User( registrationForm.getEmail(), registrationForm.getName(),
                    registrationForm.getSurname(), registrationForm.getPassword() );

            String token = issueToken( userToBeRegistered, registrationForm.getIdDevice() );
            // Return the token on the response
            return buildResponseToken( token );
        }
        //if UserNotRegisteredException is not thrown the user already exist and so he cannot register himself again
        return HttpResponseBuilder.unauthorized();
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
            return HttpResponseBuilder.forbidden();
        } catch (UserNotRegisteredException e2) {
            return HttpResponseBuilder.unauthorized();
        }
        // Issue a token for the user
        String token = issueToken( user, credentials.getIdDevice() );
        // Return the token on the response
        return buildResponseToken( token );
    }

    @Path("/manage-user")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editProfile( RegistrationForm updatedUserInfo ){

        User user;
        try {
            user = loadUser( updatedUserInfo.getEmail() );
        } catch ( UserNotRegisteredException e ) {
            return HttpResponseBuilder.badRequest();
        }

        if ( ! user.getPassword().equals( updatedUserInfo.getPassword() ) ){
            return HttpResponseBuilder.unauthorized();
        }
        user.setName( updatedUserInfo.getName() );
        user.setSurname( updatedUserInfo.getSurname() );
        user.save();
        return buildResponseToken( issueToken( user,updatedUserInfo.getIdDevice() ) );
    }

    @Path("/manage-user/{ email }/{ pws }") //nb DELETE HTTP method cannot contain a body
    @DELETE
    public Response deleteProfile( @PathParam("email") String email, @PathParam("pws") String password){
        //TODO timeout of tot days or email confirm?
        //TODO password decrypt
        User user;
        try {
            user = loadUser( email );
        } catch ( UserNotRegisteredException e ) {
            return HttpResponseBuilder.badRequest();
        }
        try {
            authenticate( user, password );
        } catch ( InvalidCredentialsException e ) {
            return HttpResponseBuilder.unauthorized();
        }
        user.remove();
        return HttpResponseBuilder.ok();
    }

    @Path( "/security/{ idDevice }" )
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response requestPublicKey( @PathParam("idDevice") String idDevice ){
        RSAEncryption encryption = new RSAEncryption( idDevice );
        PublicKey publicKey = encryption.getPublicKey();
        //TODO timer
        return HttpResponseBuilder.buildOkResponse( new PublicKeyResponse( publicKey ) );
    }

    @Path( "/security" )
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response askNewCredentials( EmailMessage emailMessage ){
        User user;

        try {
            user = loadUser( emailMessage.getEmail() );
        } catch ( UserNotRegisteredException e ) {
            return HttpResponseBuilder.badRequest();
        }

        EmailSender.sendNewCredentials( user );
        return HttpResponseBuilder.ok();
    }

    /**
     * Find out if exist a user with the specified credentials
     * @param email username of the user
     * @param password password of the user
     * @throws InvalidCredentialsException if not exist an user with the specified credentials
     * @throws UserNotRegisteredException if the user credential specified are not registered in the system
     */
    private User authenticate( String email, String password ) throws InvalidCredentialsException, UserNotRegisteredException {
        User userToBeAuthenticated = loadUser( email );
        authenticate( userToBeAuthenticated, password);
        return userToBeAuthenticated;
    }

    private String issueToken( User user, String idDevice) {
        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be associated to a user
        // Return the issued token
        //if a token already exists it replaces it
        UserDevice userDevice;
        try {
            userDevice = UserDevice.load( idDevice );
            //the following instruction is executed only if an instance of that device already exist
            userDevice.remove();
        } catch ( EntityNotFoundException e ) {
            //nothing if it not exists is ok, we are creating it
        }

        user.addUserDevice( idDevice );
        user.save();
        return user.getUserDevice( idDevice ).getUnivocalCode() ;
    }

    private Response buildResponseToken(String token){
        return HttpResponseBuilder.buildOkResponse( new TokenResponse( token ) );
    }

    private User loadUser( String email ) throws UserNotRegisteredException {
        User user;
        try {
            user = User.load( email );
        } catch ( EntityNotFoundException e ) {
            throw new UserNotRegisteredException();
        }
        return user;
    }

    private void authenticate( User userToBeAuthenticated, String password ) throws InvalidCredentialsException{
        if ( ! userToBeAuthenticated.getPassword().equals( password ) ){
            throw new InvalidCredentialsException();
        }
    }
}