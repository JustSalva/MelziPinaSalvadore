package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.HelloWorld;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

//Defines the base URI for all resource URIs.
@ApplicationPath("/")
//The java class declares root resource and provider classes
public class TravlendarRESTful extends Application{
    //The method returns a non-empty collection with classes, that must be included in the published JAX-RS application
    @Override
    public Set<Class<?>> getClasses() {
        HashSet h = new HashSet<Class<?>>();
        h.add(EventRESTful.class);
        h.add(ScheduleRESTful.class);
        h.add(PathRESTful.class);
        h.add(PreferenceRESTful.class);
        h.add(AuthenticationRESTful.class);
        h.add(TripRESTful.class);
        return h;
    }
}

