package it.polimi.travlendarplus.RESTful;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.ApplicationPath;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines the base URI for all resource URIs.
 * This java class declares root resource and provider classes
 */
@ApplicationPath( "/" )
public class TravlendarRESTful extends ResourceConfig {

    /**
     * This method returns a non-empty collection with classes, that must be
     * included in the published JAX-RS web services
     */
    public TravlendarRESTful () {
        // Register resources and providers using package-scanning.
        packages( "it.polimi.travlendarplus.RESTful" );

        // Register an instance of LoggingFilter.
        register( new LoggingFeature( Logger.getLogger( "inbound" ),
                Level.ALL, LoggingFeature.Verbosity.PAYLOAD_ANY, 8192 ) );

        // Enable Tracing support
        property( ServerProperties.TRACING, "ALL" );
    }

}

