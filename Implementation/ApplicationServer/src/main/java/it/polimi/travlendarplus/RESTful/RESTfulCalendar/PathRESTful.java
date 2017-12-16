package it.polimi.travlendarplus.RESTful.RESTfulCalendar;

import it.polimi.travlendarplus.RESTful.authenticationManager.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.authenticationManager.Secured;
import it.polimi.travlendarplus.beans.calendarManager.PathManager;
import it.polimi.travlendarplus.entities.User;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class provide all RESTful methods related to handle the users travel-paths
 * This RESTful resource will be hosted at the relative URI path "/path"
 */
@Path( "/path" )
@Secured
public class PathRESTful {

    /**
     * Enterprise Java beans that offers the logic related to path-related functionalities
     */
    @EJB
    private PathManager pathManager;

    /**
     * User that performs a request, automatically injected after his authentication
     */
    @Inject
    @AuthenticatedUser
    private User authenticatedUser;

    /**
     * This method initialize the injected event manager with the user to be handled
     * It will be executed before any request is actually performed
     */
    @PostConstruct
    public void postConstruct () {
        pathManager.setCurrentUser( authenticatedUser );
    }

    /**
     * It allows the  user to obtain the information of one path
     *
     * @param idEvent the identifier of the event associated to the requested path
     * @return an HTTP 400 Bad Request response status code if the specified event does not exist,
     * an HTTP 409 Conflict response status code if the event is not scheduled ( and so it hasn't a correlated path )
     * otherwise HTTP 200 OK success status response code and the information
     * of the requested path in the message body
     */
    //it returns info on best path related to the specified event
    @Path( "{idEvent}" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getBestPathInfo ( @PathParam( "idEvent" ) long idEvent ) {
        /*try {
            return HttpResponseBuilder.buildOkResponse( pathManager.getBestPathInfo( idEvent ) );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        } catch ( NotScheduledException e1 ){
            return HttpResponseBuilder.conflict();
        }*/
        return null; //TODO
    }

    /**
     * It allows the user to select one of the alternatives path from the ones he has already obtained
     *
     * @param idNewPath identifier of the selected path
     * @return an HTTP 400 Bad Request response status code if the specified path does not exist,
     * or an HTTP 200 OK success status response code with the complete info of the selected path
     * in the message body
     */
    //the user select another best path for a certain event
    @Path( "{idNewPath}" )
    @POST
    @Produces( MediaType.APPLICATION_JSON )
    public Response changeBestPath ( @PathParam( "idNewPath" ) long idNewPath ) {
        /*try {
            return HttpResponseBuilder.buildOkResponse( pathManager.changePath( idNewPath) );
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }*/
        return null; //TODO
    }

    /**
     * Allows the user to request a list of alternatives path to reach an event
     *
     * @param eventId identifier of the event
     * @return an HTTP 400 Bad Request response status code if the specified event does not exist,
     * or an HTTP 200 OK success status response code with the requested paths in the message body
     */
    @Path( "changePath/{eventId}" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response performAlternativeRequests ( @PathParam( "eventId" ) long eventId ) {
        /*try {
            return HttpResponseBuilder.buildOkResponse( pathManager.getAlternativesPaths( eventId ));
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest();
        }*/
        return null; //TODO
    }


    /**
     * It allows the user to retrieve info needed to draw the path into the map
     *
     * @param idEvent identifier of the event the path is related to
     * @return an HTTP 400 Bad Request response status code if the specified event does not exist,
     * an HTTP 409 Conflict response status code if the event is not scheduled ( and so it hasn't a correlated path )
     * otherwise HTTP 200 OK success status response code and the requested information needed
     * in order to draw the path into the map ( in the message body )
     */
    @Path( "map/{idEvent}" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getPathDrawingInfo ( @PathParam( "idEvent" ) long idEvent ) {
        /*try {
            //NB this method must return only a polyline string, if not a response message must be defined
            return HttpResponseBuilder.buildOkResponse( pathManager.getDrawingPathInfo( idEvent ));
        } catch ( EntityNotFoundException e ) {
            return HttpResponseBuilder.badRequest(); //if the event does not exist
        } catch ( NotScheduledException e1 ){
            return HttpResponseBuilder.conflict();
        }*/
        return null; //TODO
    }

}
