package it.polimi.travlendarplus.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.response.BreakEventResponse;
import it.polimi.travlendarplus.retrofit.response.EventResponse;
import it.polimi.travlendarplus.retrofit.response.GetGenericEventsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleEventController implements Callback<GetGenericEventsResponse> {

    private Handler handler;

    public ScheduleEventController(Handler handler) {
        this.handler = handler;
    }

    public void start(String authToken, int idEvent) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<GetGenericEventsResponse> call = client.scheduleEvent(idEvent);
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
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<GetGenericEventsResponse> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}