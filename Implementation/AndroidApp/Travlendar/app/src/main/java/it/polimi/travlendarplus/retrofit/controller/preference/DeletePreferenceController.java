package it.polimi.travlendarplus.retrofit.controller.preference;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs an delete preference request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class DeletePreferenceController implements Callback<Void> {

    private Handler handler;

    public DeletePreferenceController(Handler handler) {
        this.handler = handler;
    }

    /**
     * Starts the server request.
     * @param authToken Authorization token.
     * @param id Id of the preference to be deleted.
     */
    public void start(String authToken, long id) {
        if (id != 0) {
            TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
            Call<Void> call = client.deletePreference(id);
            call.enqueue(this);
        } else {
            // User is trying to delete the normal preference.
            Message msg = handler.obtainMessage(1);
            msg.sendToTarget();
        }
    }
    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        if(!response.isSuccessful()) {
            Log.d("ERROR_RESPONSE", response.toString());
        }
        Message msg = handler.obtainMessage(response.code());
        msg.sendToTarget();
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
