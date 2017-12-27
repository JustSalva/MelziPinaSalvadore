package it.polimi.travlendarplus.activity.handler.event;


import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import it.polimi.travlendarplus.activity.TravelTicketActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

public class SelectTravelHandler extends DefaultHandler {

    private TravelTicketActivity activity;

    public SelectTravelHandler(Looper looper, Context context, TravelTicketActivity activity) {
        super(looper, context);
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                int eventId = msg.getData().getInt("Id");
                // Notify the user that the event has been removed.
                Toast.makeText(context, "Event removed!", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
