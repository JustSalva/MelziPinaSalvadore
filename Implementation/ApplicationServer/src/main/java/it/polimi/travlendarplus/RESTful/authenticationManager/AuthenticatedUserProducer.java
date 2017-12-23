package it.polimi.travlendarplus.RESTful.authenticationManager;

import it.polimi.travlendarplus.entities.User;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

/**
 * This request-scoped resource receive the injection of an authenticated user
 * It concretely inject in all context that requires an authenticated user
 *
 * @see AuthenticatedUser
 * @see AuthenticationFilter
 */
@RequestScoped
public class AuthenticatedUserProducer {

    @Produces
    @RequestScoped
    @AuthenticatedUser
    private User authenticatedUser;

    public void handleAuthenticationEvent ( @Observes @AuthenticatedUser User user ) {
        this.authenticatedUser = user;
    }
}
