package it.polimi.travlendarplus.retrofit.controller.event;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.body.BreakEventBody;
import it.polimi.travlendarplus.retrofit.body.EventBody;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs an add event request to the server.
 * Fills a message to be sent to the desired handler.
 */
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
            // Get the ErrorResponse containing error messages sent by the server.
            try {
                ErrorResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
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
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
