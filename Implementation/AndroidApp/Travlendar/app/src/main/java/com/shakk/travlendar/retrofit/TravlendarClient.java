package com.shakk.travlendar.retrofit;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TravlendarClient {
    @POST("register")
    Call<RegisterResponse> register(
            @Body LoginBody registerBody
    );

    @POST("login")
    Call<LoginResponse> login(
            @Body LoginBody loginBody
    );

    @GET("preference/location")
    Call getLocations(

    );

    @POST("preference/location")
    Call addLocation(

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
