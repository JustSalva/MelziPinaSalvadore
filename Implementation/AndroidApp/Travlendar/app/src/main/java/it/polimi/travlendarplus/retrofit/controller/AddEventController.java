package it.polimi.travlendarplus.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.body.BreakEventBody;
import it.polimi.travlendarplus.retrofit.body.EventBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventController implements Callback<ResponseBody> {

    private Handler handler;

    public AddEventController(Handler handler) {
        this.handler = handler;
    }

    public void start(String authToken, EventBody eventBody) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<ResponseBody> call = client.addEvent(eventBody);
        call.enqueue(this);
    }

    public void start(String authToken, BreakEventBody breakEventBody) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<ResponseBody> call = client.addBreakEvent(breakEventBody);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        Bundle bundle = new Bundle();
        if (!response.isSuccessful()) {
            Log.d("ERROR_RESPONSE", response.errorBody().contentType().toString());
            bundle.putString("Invalid", response.errorBody().contentType().toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
