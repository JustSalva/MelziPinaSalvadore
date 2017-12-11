package it.polimi.travlendarplus.RESTful.security;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.email.EmailInterface;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.MailPasswordForwardingFailedException;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.UserNotRegisteredException;
import it.polimi.travlendarplus.messages.authenticationMessages.EmailMessage;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class NewCredentialsAuthenticator {

    @Inject
    EmailInterface emailSender;

    @Path( "/security" )
    @PATCH
    @Produces( MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response askNewCredentials( EmailMessage emailMessage ){
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
