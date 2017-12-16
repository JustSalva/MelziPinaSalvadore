package com.shakk.travlendar.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.shakk.travlendar.retrofit.ServiceGenerator;
import com.shakk.travlendar.retrofit.TravlendarClient;
import com.shakk.travlendar.retrofit.body.LocationBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLocationController implements Callback<Void> {

    private Handler handler;

    public AddLocationController(Handler handler) {
        this.handler = handler;
    }

    public void start(String authToken, String name, String address, String latitude, String longitude) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<Void> call = client.addLocation(new LocationBody(name, address, latitude, longitude));
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        if(!response.isSuccessful()) {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
