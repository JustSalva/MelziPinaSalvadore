package it.polimi.travlendarplus.activity.handler.ticket;


import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.TicketEditorActivity;
import it.polimi.travlendarplus.activity.TicketsViewerActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

/**
 * Handler that handles the server response to the addTicket request.
 * It is used by the TicketEditorActivity.
 */
public class AddTicketHandler extends DefaultHandler < TicketEditorActivity > {

    //private TicketEditorActivity activity;

    public AddTicketHandler ( Looper looper, TicketEditorActivity activity ) {
        super( looper, activity );
        //this.activity = activity;
    }

    @Override
    public void handleMessage ( Message msg ) {
        super.handleMessage( msg );
        switch ( msg.what ) {
            case 200:
                Toast.makeText( activity, "Tickets added!", Toast.LENGTH_LONG ).show();
                // Launch TicketViewerActivity where tickets are updated.
                activity.startActivity( new Intent( activity, TicketsViewerActivity.class ) );
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
