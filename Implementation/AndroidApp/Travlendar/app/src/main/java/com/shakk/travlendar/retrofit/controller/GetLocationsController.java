package com.shakk.travlendar.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.shakk.travlendar.Location;
import com.shakk.travlendar.activity.AccountActivity;
import com.shakk.travlendar.retrofit.ServiceGenerator;
import com.shakk.travlendar.retrofit.TravlendarClient;
import com.shakk.travlendar.retrofit.response.GetLocationsResponse;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetLocationsController implements Callback<List<Location>> {

    private Handler handler;

    public GetLocationsController(Handler handler) {
        this.handler = handler;
    }

    public void start(String authToken) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<List<Location>> call = client.getLocations();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
        Bundle bundle = new Bundle();
        if(response.isSuccessful()) {
            String jsonLocations = new Gson().toJson(response.body());
            bundle.putString("jsonLocations", jsonLocations);
        } else {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<List<Location>> call, Throwable t) {

    }
}
