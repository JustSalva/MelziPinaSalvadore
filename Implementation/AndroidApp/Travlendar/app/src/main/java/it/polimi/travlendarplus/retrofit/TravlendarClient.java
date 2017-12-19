package it.polimi.travlendarplus.retrofit;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.retrofit.body.BreakEventBody;
import it.polimi.travlendarplus.retrofit.body.EventBody;
import it.polimi.travlendarplus.retrofit.body.LocationBody;
import it.polimi.travlendarplus.retrofit.body.LoginBody;
import it.polimi.travlendarplus.retrofit.body.PreferenceBody;
import it.polimi.travlendarplus.retrofit.body.RegisterBody;
import it.polimi.travlendarplus.retrofit.response.BreakEventResponse;
import it.polimi.travlendarplus.retrofit.response.EventResponse;
import it.polimi.travlendarplus.retrofit.response.GenericEventsResponse;
import it.polimi.travlendarplus.retrofit.response.LoginResponse;
import it.polimi.travlendarplus.retrofit.response.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TravlendarClient {
    @POST("register")
    Call<RegisterResponse> register(
            @Body RegisterBody registerBody
    );

    @POST("login")
    Call<LoginResponse> login(
            @Body LoginBody loginBody
    );

    @GET("preference/location")
    Call<List<Location>> getLocations();

    @POST("preference/location")
    Call<Void> addLocation(
            @Body LocationBody locationBody
    );

    @DELETE("preference/location/{name}")
    Call<Void> deleteLocation(
            @Path("name") String name
    );

    @GET("preference")
    Call<List<Preference>> getPreferences();

    @POST("preference")
    Call<Preference> addPreference(
            @Body PreferenceBody preferenceBody
    );

    @DELETE("preference/{id}")
    Call<Void> deletePreference(
        @Path("id") long id
    );

    @PATCH("preference")
    Call<Preference> modifyPreference(
            @Body PreferenceBody preferenceBody
    );

    @GET("event/updateLocalDb/{timestamp}")
    Call<GenericEventsResponse> getEvents(
            @Path("timestamp") long timestamp
    );

    @POST("event")
    Call<Void> addEvent(
            @Body EventBody eventBody
    );

    @POST("event/breakEvent")
    Call<Void> addBreakEvent(
            @Body BreakEventBody breakEventBody
    );
}
