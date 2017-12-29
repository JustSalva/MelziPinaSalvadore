package it.polimi.travlendarplus.activity.handler.ticket;


import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import it.polimi.travlendarplus.activity.TicketsViewerActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.activity.tasks.InsertTicketsTask;
import it.polimi.travlendarplus.retrofit.response.ticket.AllTicketsResponse;

/**
 * Handler that handles the server response to the getTickets request.
 * It is used by the TicketsViewerActivity.
 */
public class GetTicketsHandler extends DefaultHandler<TicketsViewerActivity> {

    //private TicketsViewerActivity activity;

    public GetTicketsHandler(Looper looper, TicketsViewerActivity activity) {
        super(looper, activity);
        //this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                Toast.makeText(activity, "Tickets updated!", Toast.LENGTH_LONG).show();
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String jsonTickets = bundle.getString("jsonTickets");
                AllTicketsResponse tickets = new Gson()
                        .fromJson(jsonTickets, AllTicketsResponse.class);
                // Saves tickets in the DB.
                new InsertTicketsTask(activity, tickets).execute();
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
