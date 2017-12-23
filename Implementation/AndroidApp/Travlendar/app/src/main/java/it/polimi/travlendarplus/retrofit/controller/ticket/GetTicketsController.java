package it.polimi.travlendarplus.retrofit.controller.ticket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.response.ticket.AllTicketsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs a get tickets request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class GetTicketsController implements Callback<AllTicketsResponse> {

    private Handler handler;

    public GetTicketsController(Handler handler) {
        this.handler = handler;
    }

    /**
     * Starts the server request.
     * @param authToken Authorization token.
     */
    public void start(String authToken) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<AllTicketsResponse> call = client.getAllTickets();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<AllTicketsResponse> call, Response<AllTicketsResponse> response) {
        // Sends tickets received to the handler.
        Bundle bundle = new Bundle();
        if (response.isSuccessful()) {
            String jsonTickets = new Gson().toJson(response.body());
            bundle.putString("jsonTickets", jsonTickets);
        } else {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<AllTicketsResponse> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
