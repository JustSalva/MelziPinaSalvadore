package com.shakk.travlendar.retrofit;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginController implements Callback<LoginResponse> {

    private Handler handler;

    public LoginController(Handler handler) {
        this.handler = handler;
    }

    public void start(String email, String password, String idDevice) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class);
        Call<LoginResponse> call = client.login(new LoginBody(email, password, idDevice));
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        Bundle bundle = new Bundle();
        if(response.isSuccessful()) {
            LoginResponse loginResponse = response.body();
            bundle.putString("name", loginResponse.getName());
            bundle.putString("surname", loginResponse.getSurname());
            bundle.putString("univocalCode", loginResponse.getUnivocalCode());
        } else {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {

    }
}
