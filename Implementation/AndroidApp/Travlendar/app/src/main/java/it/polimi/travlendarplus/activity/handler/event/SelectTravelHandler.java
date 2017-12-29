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
import it.polimi.travlendarplus.activity.tasks.SelectTicketTask;

/**
 * Handler that handles the server response to the selectTicket request.
 */
public class SelectTravelHandler extends DefaultHandler<TravelTicketActivity> {

    //private TravelTicketActivity activity;

    public SelectTravelHandler(Looper looper, TravelTicketActivity activity) {
        super(looper, activity);
        //this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                int ticketId = msg.getData().getInt("TicketId");
                int travelComponentId = msg.getData().getInt("TravelComponentId");
                if (msg.getData().getBoolean("Select")) {
                    // Select went good.
                    Toast.makeText(activity, "Ticket associated to travel!", Toast.LENGTH_LONG).show();
                    // Add linked ticket to travelComponent.
                    new SelectTicketTask(activity, true, ticketId, travelComponentId).execute();
                } else {
                    // Deselect went good.
                    Toast.makeText(activity, "Ticket removed from travel!", Toast.LENGTH_LONG).show();
                    // Remove linked ticket from travelComponent.
                    new SelectTicketTask(activity, false, ticketId, travelComponentId).execute();
                }
                break;
            case 500:
                Toast.makeText(activity, "Wrong operation!", Toast.LENGTH_LONG).show();
                Log.d("ERROR", "Ticket already selected/deselected");
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
