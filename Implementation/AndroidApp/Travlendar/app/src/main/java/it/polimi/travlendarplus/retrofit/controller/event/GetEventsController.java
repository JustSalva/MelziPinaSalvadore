package it.polimi.travlendarplus.retrofit.controller.event;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.response.event.GetGenericEventsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs a get events request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class GetEventsController implements Callback<GetGenericEventsResponse> {

    private Handler handler;

    public GetEventsController(Handler handler) {
        this.handler = handler;
    }

    /**
     * Starts the server request.
     * @param authToken Authorization token.
     * @param timestamp Timestamp of the last getEvent.
     */
    public void start(String authToken, long timestamp) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<GetGenericEventsResponse> call = client.getEvents(timestamp);
        call.enqueue(this);
    }
    @Override
    public void onResponse(Call<GetGenericEventsResponse> call, Response<GetGenericEventsResponse> response) {
        Bundle bundle = new Bundle();
        if (response.isSuccessful()) {
            String jsonEvents = new Gson().toJson(response.body().getUpdatedEvents());
            bundle.putString("jsonEvents", jsonEvents);
            String jsonBreakEvents = new Gson().toJson(response.body().getUpdatedBreakEvents());
            bundle.putString("jsonBreakEvents", jsonBreakEvents);
        } else {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<GetGenericEventsResponse> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
