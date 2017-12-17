package com.shakk.travlendar.retrofit.controller;


import android.os.Handler;

import com.shakk.travlendar.Preference;
import com.shakk.travlendar.retrofit.ServiceGenerator;
import com.shakk.travlendar.retrofit.TravlendarClient;
import com.shakk.travlendar.retrofit.body.PreferenceBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyPreferenceController implements Callback<Preference> {

    private Handler handler;

    public ModifyPreferenceController(Handler handler) {
        this.handler = handler;
    }

    public void start(String authToken, PreferenceBody preferenceBody) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<Preference> call = client.modifyPreference(preferenceBody);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Preference> call, Response<Preference> response) {

    }

    @Override
    public void onFailure(Call<Preference> call, Throwable t) {

    }
}
