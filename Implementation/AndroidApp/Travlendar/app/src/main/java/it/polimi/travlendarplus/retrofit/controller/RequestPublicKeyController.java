package it.polimi.travlendarplus.retrofit.controller;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.response.PublicKeyResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs a get public key request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class RequestPublicKeyController implements Callback < PublicKeyResponse > {

    private Handler handler;

    public RequestPublicKeyController ( Handler handler ) {
        this.handler = handler;
    }

    /**
     * Starts the request with the right parameters.
     *
     * @param idDevice id representing the device that needs the public key.
     */
    public void start ( String idDevice ) {
        TravlendarClient client = ServiceGenerator.createService( TravlendarClient.class );
        Call < PublicKeyResponse > call = client.requestPublicKey( idDevice );
        call.enqueue( this );
    }

    @Override
    public void onResponse ( Call < PublicKeyResponse > call, Response < PublicKeyResponse > response ) {
        Bundle bundle = new Bundle();
        if ( response.isSuccessful() ) {
            byte[] bytesPublicKey = response.body().getPublicKey();
            bundle.putByteArray( "bytesPublicKey", bytesPublicKey );
        } else {
            Log.d( "ERROR_RESPONSE", response.toString() );
        }
        Message msg = handler.obtainMessage( response.code() );
        msg.setData( bundle );
        msg.sendToTarget();
    }

    @Override
    public void onFailure ( Call < PublicKeyResponse > call, Throwable t ) {
        Log.d( "INTERNET_CONNECTION", "ABSENT" );
        Message msg = handler.obtainMessage( 0 );
        msg.sendToTarget();
    }
}
