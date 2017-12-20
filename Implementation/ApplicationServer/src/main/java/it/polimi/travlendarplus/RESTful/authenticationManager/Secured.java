package it.polimi.travlendarplus.RESTful.authenticationManager;


import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Tag that identifies all the secured resources of
 * Travlendar+'s Application Server
 *
 * @see AuthenticationFilter
 */
@NameBinding
@Retention( RUNTIME )
@Target( { TYPE, METHOD } )
public @interface Secured {
}
