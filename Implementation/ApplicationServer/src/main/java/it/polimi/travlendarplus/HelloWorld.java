package it.polimi.travlendarplus;

import it.polimi.travlendarplus.entities.GeneralEntity;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.LocationId;
import it.polimi.travlendarplus.entities.calendar.DateOfCalendar;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;


// The Java class will be hosted at the URI path "/helloworld"
@Path("/helloworld")
public class HelloWorld {

    @Path("IdTest/{id}")
    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces(MediaType.APPLICATION_JSON)
    public Location getClichedMessage(@PathParam("id") Integer id) {
        // Return some cliched textual content
        /*Location ogg = new Location(id,id,"");
        ogg.save();*/
        Location ogg = GeneralEntity.loadHelper(Location.class, new LocationId((long)id));
        return ogg;
    }
   /* @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response function(Oggetto oggetto){
        String result = "Product created : " + oggetto;
        return Response.status(201).entity(result).build();
    }*/

}