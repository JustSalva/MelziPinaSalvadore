package com.shakk.travlendar.retrofit;

import com.shakk.travlendar.Location;
import com.shakk.travlendar.Preference;
import com.shakk.travlendar.retrofit.body.BreakEventBody;
import com.shakk.travlendar.retrofit.body.EventBody;
import com.shakk.travlendar.retrofit.body.LocationBody;
import com.shakk.travlendar.retrofit.body.LoginBody;
import com.shakk.travlendar.retrofit.body.PreferenceBody;
import com.shakk.travlendar.retrofit.body.RegisterBody;
import com.shakk.travlendar.retrofit.response.BreakEventResponse;
import com.shakk.travlendar.retrofit.response.EventResponse;
import com.shakk.travlendar.retrofit.response.GenericEventsResponse;
import com.shakk.travlendar.retrofit.response.LoginResponse;
import com.shakk.travlendar.retrofit.response.RegisterResponse;

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
    Call<EventResponse> addEvent(
            @Body EventBody eventBody
    );

    @POST("event/breakEvent")
    Call<BreakEventResponse> addbreakEvent(
            @Body BreakEventBody breakEventBody
    );
}
