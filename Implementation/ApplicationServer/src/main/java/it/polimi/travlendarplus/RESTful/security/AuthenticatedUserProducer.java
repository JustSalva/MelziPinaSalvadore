package it.polimi.travlendarplus.RESTful.security;

import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

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
        try {
            return User.load( email );
        } catch ( EntityNotFoundException e ) {
            /*NB at this point the entity existance has already been proved,
            * so this branch should never be executed
            * TODO
            */
            return null;
        }
    }
}
