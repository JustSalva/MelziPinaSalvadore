package it.polimi.travlendarplus.RESTful.authenticationManager;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.RESTful.messages.authenticationMessages.Credentials;
import it.polimi.travlendarplus.RESTful.messages.authenticationMessages.LoginResponse;
import it.polimi.travlendarplus.RESTful.messages.authenticationMessages.RegistrationForm;
import it.polimi.travlendarplus.RESTful.messages.authenticationMessages.TokenResponse;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.UserDevice;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.InvalidCredentialsException;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.UserNotRegisteredException;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.InvalidFieldException;
import it.polimi.travlendarplus.exceptions.calendarManagerExceptions.WrongFields;
import it.polimi.travlendarplus.exceptions.encryptionExceptions.DecryptionFailedException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * This RESTful class manage the authentication process
 */
@Path( "/" )
public class AuthenticationEndpoint {

    /**
     * Allows the user to register himself into the system
     *
     * @param registrationForm message containing all required info to register an user
     * @return an HTTP 200 OK success status response code if the request is fulfilled ( and a token in the body )
     * or an HTTP 401 Unauthorized response status code if the user is already registered
     * or an HTTP 408 Request Timeout response status code if the key requested before is expired
     * or otherwise an HTTP 400 Bad Request response status code
     * ( that means there are invalid fields, the wrong ones are specified in the message body, if the body
     * is empty the decryption of the password has failed )
     */
    @Path( "/register" )
    @POST
    @Produces( MediaType.APPLICATION_JSON )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response register ( RegistrationForm registrationForm ) {

        User userToBeRegistered;
        try {
            checkRegistrationForm( registrationForm );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        }
        try {
            loadUser( registrationForm.getEmail() );
        } catch ( UserNotRegisteredException e ) {
            try {
                userToBeRegistered = new User( registrationForm.getEmail(), registrationForm.getName(),
                        registrationForm.getSurname(), registrationForm.getPassword() );
            } catch ( EntityNotFoundException e1 ) {
                return HttpResponseBuilder.requestTimeout();
            } catch ( DecryptionFailedException e2 ) {
                return HttpResponseBuilder.badRequest();
            }

            String token = issueToken( userToBeRegistered, registrationForm.getIdDevice() );
            // Return the token on the response
            return buildResponseToken( token );
        }
        //if UserNotRegisteredException is not thrown the user already exist and so he cannot register himself again
        return HttpResponseBuilder.unauthorized();
    }

    /**
     * Allows the user to log himself into the system
     *
     * @param credentials message containing all required info to recognize an user
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * ( in the body name and surname of the user and a token)
     * or an an HTTP 401 Unauthorized response status code if the user is not registered
     * or an HTTP 403 Forbidden response status code if the credential are incorrect,
     * or an HTTP 408 Request Timeout response status code if the key requested before is expired
     * or an HTTP 400 Bad Request response status code
     * ( that means there are invalid fields, the wrong ones are specified in the message body, if the body
     * is empty the decryption of the password has failed)
     */
    @Path( "/login" )
    @POST
    @Produces( MediaType.APPLICATION_JSON )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response submitLogin ( Credentials credentials ) {
        User user;
        try {
            checkCredentials( credentials );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        }
        try {
            // Authenticate the user using the credentials provided
            user = authenticate( credentials.getEmail(), credentials.getPassword() );
        } catch ( InvalidCredentialsException e1 ) {
            return HttpResponseBuilder.forbidden();
        } catch ( UserNotRegisteredException e2 ) {
            return HttpResponseBuilder.unauthorized();
        } catch ( EntityNotFoundException e3 ) {
            return HttpResponseBuilder.requestTimeout();
        } catch ( DecryptionFailedException e4 ) {
            return HttpResponseBuilder.badRequest();
        }
        // Issue a token for the user
        String token = issueToken( user, credentials.getIdDevice() );
        // Return the token on the response
        return buildLoginTokenResponse( token, user );
    }

    /**
     * Allows the user to modify his account info
     *
     * @param updatedUserInfo message containing the user credentials and the modified infos
     * @return an HTTP 200 OK success status response code if the request is fulfilled (and in the body a token)
     * or an an HTTP 401 Unauthorized response status code if the credential are incorrect
     * or an HTTP 400 Bad Request response status code if hte specified user is not registered,
     * or an HTTP 400 Bad Request response status code with a body not empty
     * ( that means there are invalid fields, the wrong ones are specified in the message body , if the body
     * is empty the decryption of the password has failed)
     */
    @Path( "/manage-user" )
    @PATCH
    @Produces( MediaType.APPLICATION_JSON )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response editProfile ( RegistrationForm updatedUserInfo ) {
        try {
            checkRegistrationForm( updatedUserInfo );
        } catch ( InvalidFieldException e ) {
            return HttpResponseBuilder.buildInvalidFieldResponse( e );
        }
        User user;
        try {
            user = loadUser( updatedUserInfo.getEmail() );
        } catch ( UserNotRegisteredException e ) {
            return HttpResponseBuilder.badRequest();
        }

        try {
            if ( !user.getPassword().equals( updatedUserInfo.getPassword() ) ) {
                return HttpResponseBuilder.unauthorized();
            }
        } catch ( EntityNotFoundException e1 ) {
            return HttpResponseBuilder.requestTimeout();
        } catch ( DecryptionFailedException e2 ) {
            return HttpResponseBuilder.badRequest();
        }
        user.setName( updatedUserInfo.getName() );
        user.setSurname( updatedUserInfo.getSurname() );
        user.save();
        return buildResponseToken( issueToken( user, updatedUserInfo.getIdDevice() ) );
    }

    /**
     * Allows the user to delete his account
     *
     * @param email email address of the user
     * @param password user's password
     * @return an HTTP 200 OK success status response code if the request is fulfilled
     * or an an HTTP 401 Unauthorized response status code if the credential are incorrect
     * or an HTTP 400 Bad Request response status code if hte specified user is not registered,
     */
    @Path( "/manage-user/{ email }/{ pws }" ) //nb DELETE HTTP method cannot contain a body
    @DELETE
    public Response deleteProfile ( @PathParam( "email" ) String email, @PathParam( "pws" ) String password ) {
        //TODO timeout of tot days or email confirm?
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

    /**
     * Find out if exist a user with the specified credentials
     *
     * @param email username of the user
     * @param password password of the user
     * @throws InvalidCredentialsException if not exist an user with the specified credentials
     * @throws UserNotRegisteredException  if the user credential specified are not registered in the system
     */
    private User authenticate ( String email, String password ) throws InvalidCredentialsException, UserNotRegisteredException {
        User userToBeAuthenticated = loadUser( email );

        authenticate( userToBeAuthenticated, password );
        return userToBeAuthenticated;
    }

    /**
     * Issue a token ( random String ), if a token already exists it replaces it
     *
     * @param user     user who identify himself with the token
     * @param idDevice device identifier at which the token is associated with
     * @return the issued token
     */
    private String issueToken ( User user, String idDevice ) {

        UserDevice userDevice;
        try {
            userDevice = UserDevice.load( idDevice );
            //the following instruction is executed only if an instance of that device already exist
            user.removeUserDevice( idDevice );
            user.save();
            userDevice.remove();
        } catch ( EntityNotFoundException e ) {
            //nothing if it not exists is ok, we are creating it
        }
        /*TODO not available in first release
          until we don't implement GCM notifications we can't handle properly the updates
          of multiple local databases, so until then only 1 device can remain logged,
          so only one token per user can be active (the tokens are correlated to exactly one user device)*/
        if ( user.getUserDevices().size() > 0 ){
            List <UserDevice>userDevicesToBeRemoved = new ArrayList <>( user.getUserDevices() );
            for ( UserDevice deviceToBeRemoved : userDevicesToBeRemoved ) {
                user.removeUserDevice( deviceToBeRemoved.getIdDevice() );
                deviceToBeRemoved.remove();
            }
            user.save();
        }

        user.addUserDevice( idDevice );
        user.save();
        return user.getUserDevice( idDevice ).getUnivocalCode();
    }

    /**
     * Build a token response
     *
     * @param token to be inserted in the response body
     * @return the response requested
     */
    private Response buildResponseToken ( String token ) {
        return HttpResponseBuilder.buildOkResponse( new TokenResponse( token ) );
    }

    /**
     * Build a token response to an user login
     *
     * @param token token to be inserted in the response body
     * @param user  who is logging in
     * @return the response requested
     */
    private Response buildLoginTokenResponse ( String token, User user ) {
        return HttpResponseBuilder.buildOkResponse( new LoginResponse( token, user.getName(), user.getSurname() ) );
    }

    /**
     * Checks if the inserted credentials are correct
     *
     * @param userToBeAuthenticated user whose credentials are to be checked
     * @param password to be validated
     * @throws InvalidCredentialsException if the password is wrong
     */
    private void authenticate ( User userToBeAuthenticated, String password ) throws InvalidCredentialsException {
        if ( !userToBeAuthenticated.getPassword().equals( password ) ) {
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Checks that the registration form fields are consistent
     *
     * @param registrationForm form to be checked
     * @throws InvalidFieldException if some fields are invalid ( which one is written into the error message )
     */
    private void checkRegistrationForm ( RegistrationForm registrationForm ) throws InvalidFieldException {
        List < String > registrationErrors = new ArrayList <>();
        if ( registrationForm.getName() == null ) {
            registrationErrors.add( WrongFields.NAME );
        }
        if ( registrationForm.getSurname() == null ) {
            registrationErrors.add( WrongFields.SURNAME );
        }
        try {
            checkCredentials( registrationForm );
        } catch ( InvalidFieldException e ) {
            registrationErrors.addAll( e.getInvalidFields() );
        }

        if ( registrationErrors.size() > 0 ) {
            throw new InvalidFieldException( registrationErrors );
        }
    }

    /**
     * Checks that the user credentials are consistent
     *
     * @param credentials credentials to be checked
     * @throws InvalidFieldException if some fields are invalid ( which one is written into the error message )
     */
    private void checkCredentials ( Credentials credentials ) throws InvalidFieldException {
        List < String > credentialErrors = new ArrayList <>();
        try {
            // Create InternetAddress object and validated the supplied
            // address which is this case is an email address.
            InternetAddress internetAddress = new InternetAddress( credentials.getEmail() );
            internetAddress.validate();
        } catch ( AddressException e ) {
            credentialErrors.add( WrongFields.EMAIL );
        }
        if ( !credentials.isPasswordConsistent() ) {
            credentialErrors.add( WrongFields.PASSWORD );
        }
        if ( credentials.getIdDevice() == null ) { //TODO ask to GMS
            credentialErrors.add( WrongFields.ID_DEVICE );
        }
        if ( credentialErrors.size() > 0 ) {
            throw new InvalidFieldException( credentialErrors );
        }
    }

    /**
     * Load the user's class instance of an user
     *
     * @param email identifier of the user
     * @return the loaded user
     * @throws UserNotRegisteredException if the user in not present in database, so he's not registered
     */
    protected static User loadUser ( String email ) throws UserNotRegisteredException {
        User user;
        try {
            user = User.load( email );
        } catch ( EntityNotFoundException e ) {
            throw new UserNotRegisteredException();
        }
        return user;
    }
}