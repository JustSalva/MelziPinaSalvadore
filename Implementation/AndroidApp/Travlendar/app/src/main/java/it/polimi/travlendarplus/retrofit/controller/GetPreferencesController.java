package it.polimi.travlendarplus.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetPreferencesController implements Callback<List<Preference>> {

    private Handler handler;

    public GetPreferencesController(Handler handler) {
        this.handler = handler;
    }

    public void start(String authToken) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<List<Preference>> call = client.getPreferences();
        call.enqueue(this);
    }
    @Override
    public void onResponse(Call<List<Preference>> call, Response<List<Preference>> response) {
        Bundle bundle = new Bundle();
        if(response.isSuccessful()) {
            String jsonPreferences = new Gson().toJson(response.body());
            bundle.putString("jsonPreferences", jsonPreferences);
        } else {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<List<Preference>> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
