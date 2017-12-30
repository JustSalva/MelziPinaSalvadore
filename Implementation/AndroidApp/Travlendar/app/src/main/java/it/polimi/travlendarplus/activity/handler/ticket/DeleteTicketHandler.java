package it.polimi.travlendarplus.activity.handler.ticket;


import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.TicketsViewerActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.activity.tasks.DeleteTicketTask;

public class DeleteTicketHandler extends DefaultHandler < TicketsViewerActivity > {

    //private TicketsViewerActivity activity;

    public DeleteTicketHandler ( Looper looper, TicketsViewerActivity activity ) {
        super( looper, activity );
        //this.activity = activity;
    }

    @Override
    public void handleMessage ( Message msg ) {
        super.handleMessage( msg );
        int ticketId = msg.getData().getInt( "Id" );
        switch ( msg.what ) {
            case 200:
                // Notify the user that the ticket has been removed.
                Toast.makeText( activity, "Ticket removed!", Toast.LENGTH_LONG ).show();
                // Remove ticket from the DB.
                new DeleteTicketTask( activity.getApplicationContext(), ticketId ).execute();
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
