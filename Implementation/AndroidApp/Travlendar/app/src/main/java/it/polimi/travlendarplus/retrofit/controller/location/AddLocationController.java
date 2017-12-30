package it.polimi.travlendarplus.retrofit.controller.location;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.body.LocationBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs an add location request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class AddLocationController implements Callback < Void > {

    private Handler handler;

    public AddLocationController ( Handler handler ) {
        this.handler = handler;
    }

    /**
     * Starts the server request.
     *
     * @param authToken Authorization token.
     * @param name      Name of the location.
     * @param address   Address of the location.
     * @param latitude  Latitude of the location.
     * @param longitude Longitude of the location.
     */
    public void start ( String authToken, String name, String address, String latitude, String longitude ) {
        TravlendarClient client = ServiceGenerator.createService( TravlendarClient.class, authToken );
        // Remove spaces to avoid errors when passing the parameter in the URL.
        String spacelessName = name.replace( " ", "_" );
        Call < Void > call = client.addLocation( new LocationBody( spacelessName, address, latitude, longitude ) );
        call.enqueue( this );
    }

    @Override
    public void onResponse ( Call < Void > call, Response < Void > response ) {
        Message msg = handler.obtainMessage( response.code() );
        msg.sendToTarget();
    }

    @Override
    public void onFailure ( Call < Void > call, Throwable t ) {
        Log.d( "INTERNET_CONNECTION", "ABSENT" );
        Message msg = handler.obtainMessage( 0 );
        msg.sendToTarget();
    }
}
