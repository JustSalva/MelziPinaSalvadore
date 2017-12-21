package it.polimi.travlendarplus.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteEventController implements Callback<Void> {

    private Handler handler;
    private int eventId;

    public DeleteEventController(Handler handler) {
        this.handler = handler;
    }

    public void start(String authToken, int id) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        this.eventId = id;
        Call<Void> call = client.deleteEvent(id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        Bundle bundle = new Bundle();
        if(!response.isSuccessful()) {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        bundle.putInt("Id", eventId);
        Message msg = handler.obtainMessage(response.code());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
