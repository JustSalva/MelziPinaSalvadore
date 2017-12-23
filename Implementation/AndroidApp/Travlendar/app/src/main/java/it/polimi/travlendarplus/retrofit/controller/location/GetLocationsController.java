package it.polimi.travlendarplus.retrofit.controller.location;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs a get locations request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class GetLocationsController implements Callback<List<Location>> {

    private Handler handler;

    public GetLocationsController(Handler handler) {
        this.handler = handler;
    }

    /**
     * Starts the server request.
     * @param authToken Authorization token.
     */
    public void start(String authToken) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<List<Location>> call = client.getLocations();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
        Bundle bundle = new Bundle();
        if(response.isSuccessful()) {
            String jsonLocations = new Gson().toJson(response.body());
            bundle.putString("jsonLocations", jsonLocations);
        } else {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<List<Location>> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
