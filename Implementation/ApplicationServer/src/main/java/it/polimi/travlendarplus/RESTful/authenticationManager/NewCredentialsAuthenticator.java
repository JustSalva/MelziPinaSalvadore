package it.polimi.travlendarplus.RESTful.authenticationManager;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.RESTful.messages.authenticationMessages.EmailMessage;
import it.polimi.travlendarplus.beans.emailManager.EmailInterface;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.MailPasswordForwardingFailedException;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.UserNotRegisteredException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path( "/" )
public class NewCredentialsAuthenticator {

    /**
     * Enterprise Java Bean that provide mail forwarding functionalities
     */
    @Inject
    private EmailInterface emailSender;

    /**
     * It allows to obtain new credentials, through the user's email
     *
     * @param emailMessage message containing the user's email
     * @return an HTTP 400 Bad Request response status code if the user is not registered or if the server
     * is unable to send the email ( in this case it is specified in the message body ),
     * an HTTP 200 OK success status response code  otherwise
     */
    @Path( "/security" )
    @PATCH
    @Produces( MediaType.APPLICATION_JSON )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response askNewCredentials ( EmailMessage emailMessage ) {
        User user;

        try {
            user = AuthenticationEndpoint.loadUser( emailMessage.getEmail() );
        } catch ( UserNotRegisteredException e ) {
            return HttpResponseBuilder.badRequest();
        }

        try {
            emailSender.sendNewCredentials( user );
        } catch ( MailPasswordForwardingFailedException e ) {
            return HttpResponseBuilder.buildBadRequest( "unable to send the email" );
        }
        return HttpResponseBuilder.ok();
    }
}
