package it.polimi.travlendarplus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.RESTful.security.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.security.Secured;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.LocationId;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.DistanceConstraint;
import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.preferences.PeriodOfDayConstraint;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.tickets.DistanceTicket;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.messages.calendarMessages.eventMessages.AddEventMessage;
import it.polimi.travlendarplus.messages.calendarMessages.eventMessages.LocationMessage;
import it.polimi.travlendarplus.messages.calendarMessages.eventMessages.PeriodMessage;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public Response getClichedMessage( @PathParam("id") Integer id) {
        // Return some cliched textual content
        /*DistanceTicket ogg = new DistanceTicket( id,new ArrayList<>(),10);
        ogg.save();
        long idTicket = ogg.getId();
        try {
            ogg = DistanceTicket.load(idTicket);
        } catch ( EntityNotFoundException e ) {
            e.printStackTrace();
        }*/
        /*TypeOfEvent typeOfEvent = new TypeOfEvent( "name", ParamFirstPath.MIN_COST );
        typeOfEvent.addDeactivated( TravelMeanEnum.CAR );
        typeOfEvent.addDeactivated( TravelMeanEnum.SHARING_BIKE );
        typeOfEvent.addConstraint( new DistanceConstraint( TravelMeanEnum.BIKE, 9,10) );
        typeOfEvent.addConstraint( new PeriodOfDayConstraint( TravelMeanEnum.BUS, 3,11 ) );
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(typeOfEvent);*/
        /*AddEventMessage addEventMessage = new AddEventMessage( "name", Instant.now(), Instant.now(),
                new PeriodMessage(  Instant.now(), Instant.now().plus( 10, ChronoUnit.DAYS ),
                        10),"description", true, true, 10,
                new LocationMessage( 45.85317, 9.39005, "Lecco" ),
                new LocationMessage( 45.46427, 9.18951, "Milano" ) );*/
        Location arrival = new Location( 1, 1, "address" );
        arrival.save();
        Location departure = new Location( 2, 2, "address" );
        departure.save();
        Event event = new Event( "name" , Instant.ofEpochSecond( 120  ), Instant.ofEpochSecond( 500 ),
                false, null, "description", false,false, null,
                arrival, departure );
        Travel travel = new Travel(  );
        event.setFeasiblePath( travel );
        User user = new User( "email", "name", "surname", "password" );
        user.save();
        event.setUser( user );
        event.save();
        return HttpResponseBuilder.buildOkResponse( event );
        /*ogg.setAddress("prova");
        ogg.save();*/
        //ogg.remove();
        //return ogg;
    }
   /* @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response function(Oggetto oggetto){
        String result = "Product created : " + oggetto;
        return Response.status(201).entity(result).build();
    }*/

}