package com.shakk.travlendar.retrofit;


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
        if(response.isSuccessful()) {
            LoginResponse loginResponse = response.body();
            Message msg = handler.obtainMessage(response.code());
            msg.sendToTarget();
            Log.d("RESPONSE", loginResponse.getName());
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {

    }
}
