package it.polimi.travlendarplus.RESTful.authenticationManager;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Interface that identify an user to be injected into the RESTful class
 * that receive a client's validated request
 *
 * @see AuthenticationFilter
 * @see AuthenticatedUserProducer
 */
@Qualifier
@Retention( RUNTIME )
@Target( { METHOD, FIELD, PARAMETER } )
public @interface AuthenticatedUser {
}