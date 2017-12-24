package it.polimi.travlendarplus.activity.handler.ticket;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.TicketEditorActivity;
import it.polimi.travlendarplus.activity.TicketsViewerActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;

/**
 * Handler that handles the server response to the addTicket request.
 * It is used by the TicketEditorActivity.
 */
public class AddTicketHandler extends DefaultHandler {

    private TicketEditorActivity activity;

    public AddTicketHandler(Looper looper, Context context, TicketEditorActivity activity) {
        super(looper, context);
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                Toast.makeText(context, "Tickets added!", Toast.LENGTH_LONG).show();
                // Launch TicketViewerActivity where tickets are updated.
                context.startActivity(new Intent(context, TicketsViewerActivity.class));
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
