package it.polimi.travlendarplus.retrofit.controller.event;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectTravelController implements Callback < Void > {

    private Handler handler;
    private int ticketId;
    private int travelComponentId;
    private boolean select;

    public SelectTravelController ( Handler handler ) {
        this.handler = handler;
    }

    /**
     * Starts the selectTravel server request.
     *
     * @param authToken         Authorization token.
     * @param ticketId          Id of the event to be selected.
     * @param travelComponentId Id of the travel component to be selected.
     */
    public void selectTravel ( String authToken, int ticketId, int travelComponentId ) {
        TravlendarClient client = ServiceGenerator.createService( TravlendarClient.class, authToken );
        this.ticketId = ticketId;
        this.travelComponentId = travelComponentId;
        this.select = true;
        Call < Void > call = client.selectTravel( ticketId, travelComponentId );
        call.enqueue( this );
    }

    /**
     * Starts the deselectTravel server request.
     *
     * @param authToken         Authorization token.
     * @param ticketId          Id of the event to be deselected.
     * @param travelComponentId Id of the travel component to be deselected.
     */
    public void deselectTravel ( String authToken, int ticketId, int travelComponentId ) {
        TravlendarClient client = ServiceGenerator.createService( TravlendarClient.class, authToken );
        this.ticketId = ticketId;
        this.travelComponentId = travelComponentId;
        this.select = false;
        Call < Void > call = client.deselectTravel( ticketId, travelComponentId );
        call.enqueue( this );
    }

    @Override
    public void onResponse ( Call < Void > call, Response < Void > response ) {
        Bundle bundle = new Bundle();
        if ( !response.isSuccessful() ) {
            Log.d( "ERROR_RESPONSE", response.toString() );
        } else {
            bundle.putBoolean( "Select", select );
            bundle.putInt( "TravelComponentId", travelComponentId );
            bundle.putInt( "TicketId", ticketId );
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
