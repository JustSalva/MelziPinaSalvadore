package it.polimi.travlendarplus.RESTful;

import it.polimi.travlendarplus.RESTful.security.Secured;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import it.polimi.travlendarplus.messages.authenticationMessages.TokenResponse;
import it.polimi.travlendarplus.messages.calendarMessages.BreakEventDescriptionMessage;
import it.polimi.travlendarplus.messages.calendarMessages.EventDescriptionMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// The Java class will be hosted at the URI path "/event"
@Path("/event")
@Secured
public class EventRESTful {

    @Path("{idEvent}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventInformation( @PathParam("idEvent") long id) {    //TODO
        try {
            Event event = Event.load( id );
            return HttpResponseBuilder.buildOkResponse( new EventDescriptionMessage( event ) );
        } catch ( EntityNotFoundException e ) {
            try {
                BreakEvent breakEvent = BreakEvent.load( id );
                return HttpResponseBuilder.buildOkResponse( new BreakEventDescriptionMessage( breakEvent ) );
            } catch ( EntityNotFoundException e1 ) {
                return HttpResponseBuilder.badRequest();
            }
        }
    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean createEvent(Event event) {   //TODO
        //this function return the result of the creation.
        return false;
    }

    @Path("alternatives/{idEvent}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getAlternatives(@PathParam("idEvent") int idEvent) {   //TODO
        return null;
    }

}
