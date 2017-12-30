package it.polimi.travlendarplus.retrofit;

import java.util.List;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.retrofit.body.BreakEventBody;
import it.polimi.travlendarplus.retrofit.body.EventBody;
import it.polimi.travlendarplus.retrofit.body.LocationBody;
import it.polimi.travlendarplus.retrofit.body.LoginBody;
import it.polimi.travlendarplus.retrofit.body.PreferenceBody;
import it.polimi.travlendarplus.retrofit.body.RegisterBody;
import it.polimi.travlendarplus.retrofit.body.ticket.DistanceTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.GenericTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.PathTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.PeriodTicketBody;
import it.polimi.travlendarplus.retrofit.response.LoginResponse;
import it.polimi.travlendarplus.retrofit.response.PublicKeyResponse;
import it.polimi.travlendarplus.retrofit.response.RegisterResponse;
import it.polimi.travlendarplus.retrofit.response.event.GetGenericEventsResponse;
import it.polimi.travlendarplus.retrofit.response.ticket.AllTicketsResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interface defining server requests that can be used in the application.
 */
public interface TravlendarClient {
    @GET( "security/{idDevice}" )
    Call < PublicKeyResponse > requestPublicKey (
            @Path( "idDevice" ) String idDevice
    );

    @POST( "register" )
    Call < RegisterResponse > register (
            @Body RegisterBody registerBody
    );

    @POST( "login" )
    Call < LoginResponse > login (
            @Body LoginBody loginBody
    );

    @GET( "preference/location" )
    Call < List < Location > > getLocations ();

    @POST( "preference/location" )
    Call < Void > addLocation (
            @Body LocationBody locationBody
    );

    @DELETE( "preference/location/{name}" )
    Call < Void > deleteLocation (
            @Path( "name" ) String name
    );

    @GET( "preference" )
    Call < List < Preference > > getPreferences ();

    @POST( "preference" )
    Call < Preference > addPreference (
            @Body PreferenceBody preferenceBody
    );

    @DELETE( "preference/{id}" )
    Call < Void > deletePreference (
            @Path( "id" ) long id
    );

    @PATCH( "preference" )
    Call < Preference > modifyPreference (
            @Body PreferenceBody preferenceBody
    );

    @GET( "event/updateLocalDb/{timestamp}" )
    Call < GetGenericEventsResponse > getEvents (
            @Path( "timestamp" ) long timestamp
    );

    @POST( "event" )
    Call < ResponseBody > addEvent (
            @Body EventBody eventBody
    );

    @POST( "event/breakEvent" )
    Call < ResponseBody > addBreakEvent (
            @Body BreakEventBody breakEventBody
    );

    @DELETE( "event/{id}" )
    Call < Void > deleteEvent (
            @Path( "id" ) int id
    );

    @PATCH( "path/{id}" )
    Call < GetGenericEventsResponse > scheduleEvent (
            @Path( "id" ) int id
    );

    @GET( "trip" )
    Call < AllTicketsResponse > getAllTickets ();

    @POST( "trip/distanceTicket" )
    Call < Void > addDistanceTicket (
            @Body DistanceTicketBody distanceTicketBody
    );

    @POST( "trip/genericTicket" )
    Call < Void > addGenericTicket (
            @Body GenericTicketBody genericTicketBody
    );

    @POST( "trip/pathTicket" )
    Call < Void > addPathTicket (
            @Body PathTicketBody pathTicketBody
    );

    @POST( "trip/periodTicket" )
    Call < Void > addPeriodTicket (
            @Body PeriodTicketBody periodTicketBody
    );

    @DELETE( "trip/{id}" )
    Call < Void > deleteTicket (
            @Path( "id" ) int id
    );

    @GET( "trip/{travelComponentId}" )
    Call < AllTicketsResponse > getCompatibleTickets (
            @Path( "travelComponentId" ) int travelComponentId
    );

    @PATCH( "trip/selectTicket/{ticketId}/{travelComponentId}" )
    Call < Void > selectTravel (
            @Path( "ticketId" ) int ticketId,
            @Path( "travelComponentId" ) int travelComponentId
    );

    @PATCH( "trip/deselectTicket/{ticketId}/{travelComponentId}" )
    Call < Void > deselectTravel (
            @Path( "ticketId" ) int ticketId,
            @Path( "travelComponentId" ) int travelComponentId
    );
}
