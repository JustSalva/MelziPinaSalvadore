package it.polimi.travlendarplus.activity.handler.ticket;


import android.os.Bundle;
import android.os.Looper;
import android.os.Message;

import com.google.gson.Gson;

import it.polimi.travlendarplus.activity.TravelTicketActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.retrofit.response.ticket.AllTicketsResponse;

public class GetCompatibleTicketsHandler extends DefaultHandler < TravelTicketActivity > {

    //private TravelTicketActivity activity;

    public GetCompatibleTicketsHandler ( Looper looper, TravelTicketActivity activity ) {
        super( looper, activity );
        //this.activity = activity;
    }

    @Override
    public void handleMessage ( Message msg ) {
        super.handleMessage( msg );
        switch ( msg.what ) {
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                int eventId = bundle.getInt( "travelComponentId" );
                String jsonTickets = bundle.getString( "jsonTickets" );
                AllTicketsResponse tickets = new Gson()
                        .fromJson( jsonTickets, AllTicketsResponse.class );
                activity.fillCompatibleTickets( eventId, tickets );
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
