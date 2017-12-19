package com.shakk.travlendar.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.shakk.travlendar.retrofit.ServiceGenerator;
import com.shakk.travlendar.retrofit.TravlendarClient;
import com.shakk.travlendar.retrofit.response.EventResponse;
import com.shakk.travlendar.retrofit.response.GenericEventsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetEventsController implements Callback<GenericEventsResponse> {

    private Handler handler;

    public GetEventsController(Handler handler) {
        this.handler = handler;
    }

    public void start(String authToken, long timestamp) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<GenericEventsResponse> call = client.getEvents(timestamp);
        call.enqueue(this);
    }
    @Override
    public void onResponse(Call<GenericEventsResponse> call, Response<GenericEventsResponse> response) {
        Bundle bundle = new Bundle();
        if (response.isSuccessful()) {
            String jsonEvents = new Gson().toJson(response.body().getUpdatedEvents());
            bundle.putString("jsonEvents", jsonEvents);
            String jsonBreakEvents = new Gson().toJson(response.body().getUpdatedBreakEvents());
            bundle.putString("jsonBreakEvents", jsonBreakEvents);
        } else {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<GenericEventsResponse> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
