package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.HelloWorld;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.ApplicationPath;
import java.util.logging.Level;
import java.util.logging.Logger;

//Defines the base URI for all resource URIs.
@ApplicationPath( "/" )
//The java class declares root resource and provider classes
public class TravlendarRESTful extends ResourceConfig {
    //The method returns a non-empty collection with classes, that must be included in the published JAX-RS application

    public TravlendarRESTful () {
        // Register resources and providers using package-scanning.
        packages( "it.polimi.travlendarplus.RESTful" );

        // Register my custom provider - not needed if it's in my.package.
        register( HelloWorld.class );
        // Register an instance of LoggingFilter.
        register( new LoggingFeature( Logger.getLogger( "inbound" ),
                Level.ALL, LoggingFeature.Verbosity.PAYLOAD_ANY, 8192 ) );

        // Enable Tracing support, to be removed before deploy.
        property( ServerProperties.TRACING, "ALL" );
    }

}

