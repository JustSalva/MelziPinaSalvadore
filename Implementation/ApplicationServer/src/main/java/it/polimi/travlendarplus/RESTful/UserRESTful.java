package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.security.Secured;

import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

// The Java class will be hosted at the URI path "/schedule"
@Path("/user")

public class UserRESTful {

    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean register() {
        //TODO: which info to pass -> user doesn't contain pwd, credentials don't contain name and surname
        return false;
    }

    //TODO
}
