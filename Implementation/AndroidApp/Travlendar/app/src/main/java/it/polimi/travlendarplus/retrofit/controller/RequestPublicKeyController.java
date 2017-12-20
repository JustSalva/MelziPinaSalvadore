package it.polimi.travlendarplus.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.security.PublicKey;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.response.PublicKeyResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestPublicKeyController implements Callback<PublicKeyResponse> {

    private Handler handler;

    public RequestPublicKeyController(Handler handler) {
        this.handler = handler;
    }

    public void start(String idDevice) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class);
        Call<PublicKeyResponse> call = client.requestPublicKey(idDevice);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<PublicKeyResponse> call, Response<PublicKeyResponse> response) {
        Bundle bundle = new Bundle();
        if (response.isSuccessful()) {
            // String jsonPublicKey = response.body();
            // bundle.putString("jsonPublicKey", jsonPublicKey);
        } else {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<PublicKeyResponse> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
