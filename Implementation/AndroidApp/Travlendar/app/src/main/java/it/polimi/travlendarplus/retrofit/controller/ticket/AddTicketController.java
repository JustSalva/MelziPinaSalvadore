package it.polimi.travlendarplus.retrofit.controller.ticket;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.body.ticket.DistanceTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.GenericTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.PathTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.PeriodTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.TicketBody;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTicketController implements Callback<Void> {

    private Handler handler;

    public AddTicketController(Handler handler) {
        this.handler = handler;
    }

    /**
     * Starts the add generic ticket request.
     * @param authToken Authorization token.
     * @param ticketBody Body containing the ticket info.
     */
    public void addGenericTicket(String authToken, GenericTicketBody ticketBody) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<Void> call = client.addGenericTicket(ticketBody);
        call.enqueue(this);
    }

    /**
     * Starts the add path ticket request.
     * @param authToken Authorization token.
     * @param ticketBody Body containing the ticket info.
     */
    public void addPathTicket(String authToken, PathTicketBody ticketBody) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<Void> call = client.addPathTicket(ticketBody);
        call.enqueue(this);
    }

    /**
     * Starts the add distance ticket request.
     * @param authToken Authorization token.
     * @param ticketBody Body containing the ticket info.
     */
    public void addDistanceTicket(String authToken, DistanceTicketBody ticketBody) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<Void> call = client.addDistanceTicket(ticketBody);
        call.enqueue(this);
    }

    /**
     * Starts the add period ticket request.
     * @param authToken Authorization token.
     * @param ticketBody Body containing the ticket info.
     */
    public void addPeriodTicket(String authToken, PeriodTicketBody ticketBody) {
        TravlendarClient client = ServiceGenerator.createService(TravlendarClient.class, authToken);
        Call<Void> call = client.addPeriodTicket(ticketBody);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        Bundle bundle = new Bundle();
        if (! response.isSuccessful()) {
            // Get the ErrorResponse containing error messages sent by the server.
            try {
                ErrorResponse errorResponse = new Gson()
                        .fromJson(response.errorBody().string(), ErrorResponse.class);
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
    public void onFailure(Call<Void> call, Throwable t) {
        Log.d("INTERNET_CONNECTION", "ABSENT");
        Message msg = handler.obtainMessage(0);
        msg.sendToTarget();
    }
}
