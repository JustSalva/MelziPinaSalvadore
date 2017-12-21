package it.polimi.travlendarplus.retrofit.controller;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs a delete location request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class DeleteLocationController implements Callback<Void> {

    private Handler handler;

    public DeleteLocationController(Handler handler) {
        this.handler = handler;
    }

    /**
     * Starts the server request.
     * @param authToken Authorization token.
     * @param name Name of the location to be deleted.
     */
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
