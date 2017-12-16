package it.polimi.travlendarplus.RESTful.authenticationManager;

import it.polimi.travlendarplus.entities.User;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

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
