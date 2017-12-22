package it.polimi.travlendarplus.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.body.PreferenceBody;

import it.polimi.travlendarplus.retrofit.response.ErrorResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs an add preference request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class AddPreferenceController implements Callback<Preference> {

    private Handler handler;

    public AddPreferenceController(Handler handler) {
        this.handler = handler;
    }

    /**
     * Starts the server request.
     * @param authToken Authorization token.
     * @param preferenceBody Body containing the preference info.
     */
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
            // Get the ErrorResponse containing error messages sent by the server.
            try {
                ErrorResponse errorResponse = new Gson()
                        .fromJson(response.errorBody().string(), ErrorResponse.class);
                for (String message : errorResponse.getMessages()) {
                    Log.d("ERROR_RESPONSE", message);
                }
                // Put the ErrorResponse in a Json to be sent to the handler.
                bundle.putString("errorResponse", new Gson().toJson(errorResponse));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
