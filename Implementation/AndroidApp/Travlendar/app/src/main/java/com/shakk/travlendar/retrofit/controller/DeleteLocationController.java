package com.shakk.travlendar.retrofit.controller;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.shakk.travlendar.retrofit.ServiceGenerator;
import com.shakk.travlendar.retrofit.TravlendarClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteLocationController implements Callback<Void> {

    private Handler handler;

    public DeleteLocationController(Handler handler) {
        this.handler = handler;
    }

    public void start(String authToken, String name) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<Void> call = client.deleteLocation(name);
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
