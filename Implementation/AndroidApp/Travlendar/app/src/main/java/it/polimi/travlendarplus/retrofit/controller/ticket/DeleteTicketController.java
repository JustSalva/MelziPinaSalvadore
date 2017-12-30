package it.polimi.travlendarplus.retrofit.controller.ticket;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Controller that performs an delete ticket request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class DeleteTicketController implements Callback < Void > {

    private Handler handler;
    private int ticketId;

    public DeleteTicketController ( Handler handler ) {
        this.handler = handler;
    }

    /**
     * Starts the server request.
     *
     * @param authToken Authorization token.
     * @param id        Id of the ticket to be deleted.
     */
    public void start ( String authToken, int id ) {
        TravlendarClient client = ServiceGenerator.createService( TravlendarClient.class, authToken );
        this.ticketId = id;
        Call < Void > call = client.deleteTicket( id );
        call.enqueue( this );
    }

    @Override
    public void onResponse ( Call < Void > call, Response < Void > response ) {
        Bundle bundle = new Bundle();
        if ( response.isSuccessful() ) {
            bundle.putInt( "Id", ticketId );
        } else {
            Log.d( "ERROR_RESPONSE", response.toString() );
        }
        Message msg = handler.obtainMessage( response.code() );
        msg.setData( bundle );
        msg.sendToTarget();
    }

    @Override
    public void onFailure ( Call < Void > call, Throwable t ) {
        Log.d( "INTERNET_CONNECTION", "ABSENT" );
        Message msg = handler.obtainMessage( 0 );
        msg.sendToTarget();
    }
}
