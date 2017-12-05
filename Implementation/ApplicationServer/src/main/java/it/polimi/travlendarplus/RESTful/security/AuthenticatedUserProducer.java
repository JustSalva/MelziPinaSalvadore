package it.polimi.travlendarplus.RESTful.security;

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

    public void handleAuthenticationEvent(@Observes @AuthenticatedUser String email) {
        this.authenticatedUser = findUser(email);
    }

    private User findUser(String email) {
        return User.load( email );
    }
}
