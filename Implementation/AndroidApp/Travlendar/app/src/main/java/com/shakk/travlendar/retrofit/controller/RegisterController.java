package com.shakk.travlendar.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.shakk.travlendar.retrofit.ServiceGenerator;
import com.shakk.travlendar.retrofit.TravlendarClient;
import com.shakk.travlendar.retrofit.body.LoginBody;
import com.shakk.travlendar.retrofit.body.RegisterBody;
import com.shakk.travlendar.retrofit.response.LoginResponse;
import com.shakk.travlendar.retrofit.response.RegisterResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterController implements Callback<RegisterResponse> {

    private Handler handler;

    public RegisterController(Handler handler) {
        this.handler = handler;
    }

    public void start(String email, String password, String idDevice, String name, String surname) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class);
        Call<RegisterResponse> call = client
                .register(new RegisterBody(email, password, idDevice, name, surname));
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
        Bundle bundle = new Bundle();
        if(response.isSuccessful()) {
            RegisterResponse registerResponse = response.body();
            bundle.putString("univocalCode", registerResponse.getUnivocalCode());
        } else {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<RegisterResponse> call, Throwable t) {

    }
}
