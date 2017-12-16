package com.shakk.travlendar.retrofit;

import com.shakk.travlendar.Location;
import com.shakk.travlendar.retrofit.body.LocationBody;
import com.shakk.travlendar.retrofit.body.LoginBody;
import com.shakk.travlendar.retrofit.body.RegisterBody;
import com.shakk.travlendar.retrofit.response.LoginResponse;
import com.shakk.travlendar.retrofit.response.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
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
    Call addLocation(
            @Body LocationBody locationBody
    );

    @DELETE("preference/location/{name}")
    Call deleteLocation(
        @Path("name") String name
    );

    @GET("preference")
    Call getPreferences(

    );

    @POST("preference")
    Call addPreference(

    );

    @DELETE("/preference/{id}")
    Call deletePreference(
        @Path("id") int id
    );
}
