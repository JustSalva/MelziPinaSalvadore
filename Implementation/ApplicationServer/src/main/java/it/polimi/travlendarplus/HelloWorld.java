package it.polimi.travlendarplus;

import it.polimi.travlendarplus.RESTful.security.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.security.Secured;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.LocationId;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.tickets.DistanceTicket;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

// The Java class will be hosted at the URI path "/prova"
@Path("/prova")
public class HelloWorld {

    @Inject
    @AuthenticatedUser
    User authenticatedUser;

    @GET
    @Secured
    @Path("/a")
    @Produces("text/plain")
    public String prova () {
        return authenticatedUser.getEmail();
    }

    @Path("IdTest/{id}")
    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces(MediaType.APPLICATION_JSON)
    public DistanceTicket getClichedMessage(@PathParam("id") Integer id) {
        // Return some cliched textual content
        DistanceTicket ogg = new DistanceTicket( id,new ArrayList<>(),10);
        ogg.save();
        long idTicket = ogg.getId();
        ogg = DistanceTicket.load(idTicket);
        /*ogg.setAddress("prova");
        ogg.save();*/
        //ogg.remove();
        return ogg;
    }
   /* @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response function(Oggetto oggetto){
        String result = "Product created : " + oggetto;
        return Response.status(201).entity(result).build();
    }*/

}