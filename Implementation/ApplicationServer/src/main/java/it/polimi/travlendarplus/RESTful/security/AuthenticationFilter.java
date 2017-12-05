package it.polimi.travlendarplus.RESTful.security;

import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.UserDevice;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.InvalidTokenException;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String REALM = "travlendar-plus/ApplicationServer";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Inject
    @AuthenticatedUser
    Event<String> userAuthenticatedEvent;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        //userAuthenticatedEvent.fire("email"); //TODO: obtain name from Token
        //TODO non posso ottenere direttamente l'user? perchè per ora è così

        // Get the Authorization header from the request
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader
                .substring(AUTHENTICATION_SCHEME.length()).trim();
        try {
            validateToken(token);
        } catch (InvalidTokenException e) {
            abortWithUnauthorized(requestContext);
        }

        userAuthenticatedEvent.fire("email");   //TODO
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());
    }

    private void validateToken( String token ) throws InvalidTokenException {
        // Check if the token was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
        if( ! UserDevice.isTokenPresent( token ))
            throw new InvalidTokenException();
    }
}
