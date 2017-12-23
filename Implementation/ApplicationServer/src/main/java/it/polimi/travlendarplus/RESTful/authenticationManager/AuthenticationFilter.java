package it.polimi.travlendarplus.RESTful.authenticationManager;

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

/**
 * This Class provide the authentication and authorization functionalities.
 * It checks that a token, received in the HTTP request header, is correct
 * and identify the user associated too it.
 * All server's protected functionalities can take advantage of this
 * functionalities simply writing the tag @Secured above the class declaration
 */
@Secured
@Provider
@Priority( Priorities.AUTHENTICATION )
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String REALM = "travlendar-plus/ApplicationServer";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    /**
     * Event that, when activated, will cause the user' class injection
     */
    @Inject
    @AuthenticatedUser
    Event < User > userAuthenticatedEvent;

    /**
     * Checks if the received token is valid, if valid inject the user into the
     * class that will handle the preformed request, otherwise it simply
     * refuse the request
     *
     * @param requestContext context of the request, it consist in the entire
     *                       HTTP request
     * @throws IOException if the request syntax is not correct
     */
    @Override
    public void filter ( ContainerRequestContext requestContext ) throws IOException {

        String authorizationHeader =
                requestContext.getHeaderString( HttpHeaders.AUTHORIZATION );

        // Validate the Authorization header
        if ( !isTokenBasedAuthentication( authorizationHeader ) ) {
            abortWithUnauthorized( requestContext );
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader
                .substring( AUTHENTICATION_SCHEME.length() ).trim();
        User user;
        try {
            user = validateToken( token );
            userAuthenticatedEvent.fire( user );
        } catch ( InvalidTokenException e ) {
            abortWithUnauthorized( requestContext );
        }
    }

    /**
     * Checks if the Authorization header is valid.
     * It must be not null and it must be prefixed with "Bearer" plus a
     * whitespace. The authentication scheme comparison must be case-insensitive
     *
     * @param authorizationHeader header of the HTTP request that is to be
     *                            authenticated
     * @return true if the requests contains a token, false otherwise
     */
    private boolean isTokenBasedAuthentication ( String authorizationHeader ) {

        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith( AUTHENTICATION_SCHEME.toLowerCase() + " " );
    }

    /**
     * Aborts the filter chain with a 401 status code response
     * The WWW-Authenticate header is sent along with the response
     *
     * @param requestContext context of the request, it consist in the entire
     *                       HTTP request
     */
    private void abortWithUnauthorized ( ContainerRequestContext requestContext ) {
        requestContext.abortWith(
                Response.status( Response.Status.UNAUTHORIZED )
                        .header( HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"" )
                        .build() );
    }

    /**
     * Checks if the token was issued by the server and if it's not expired
     * Throws an Exception if the token is invalid
     *
     * @param token HTTP - request's token to be checked
     * @return the authenticated user
     * @throws InvalidTokenException if does not exist an user associated
     *                               with the token
     */
    private User validateToken ( String token ) throws InvalidTokenException {
        return UserDevice.findUserRelativeToToken( token );
    }
}
