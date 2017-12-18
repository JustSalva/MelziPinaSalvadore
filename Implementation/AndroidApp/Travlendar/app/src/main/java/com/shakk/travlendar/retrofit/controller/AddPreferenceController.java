package com.shakk.travlendar.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.shakk.travlendar.Preference;
import com.shakk.travlendar.retrofit.ServiceGenerator;
import com.shakk.travlendar.retrofit.TravlendarClient;
import com.shakk.travlendar.retrofit.body.PreferenceBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPreferenceController implements Callback<Preference> {

    private Handler handler;

    public AddPreferenceController(Handler handler) {
        this.handler = handler;
    }

    public void start(String authToken, PreferenceBody preferenceBody) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<Preference> call = client.addPreference(preferenceBody);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Preference> call, Response<Preference> response) {
        Bundle bundle = new Bundle();
        if (response.isSuccessful()) {
            String jsonPreference = new Gson().toJson(response.body());
            bundle.putString("jsonPreference", jsonPreference);
        } else {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<Preference> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
