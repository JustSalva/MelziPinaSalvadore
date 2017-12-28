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
                int ticketId = msg.getData().getInt("TicketId");
                int travelComponentId = msg.getData().getInt("TravelComponentId");
                if (msg.getData().getBoolean("Select")) {
                    // Select went good.
                    Toast.makeText(context, "Ticket associated to travel!", Toast.LENGTH_LONG).show();
                    // Add linked ticket to travelComponent.
                    new SelectTicketTask(context, true, ticketId, travelComponentId).execute();
                } else {
                    // Deselect went good.
                    Toast.makeText(context, "Ticket removed from travel!", Toast.LENGTH_LONG).show();
                    // Remove linked ticket from travelComponent.
                    new SelectTicketTask(context, false, ticketId, travelComponentId).execute();
                }
                break;
            case 500:
                Toast.makeText(context, "Wrong operation!", Toast.LENGTH_LONG).show();
                Log.d("ERROR", "Ticket already selected/deselected");
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
