package it.polimi.travlendarplus.retrofit.controller.preference;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.body.PreferenceBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs an editing preference request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class ModifyPreferenceController implements Callback < Preference > {

    private Handler handler;

    public ModifyPreferenceController ( Handler handler ) {
        this.handler = handler;
    }

    /**
     * Starts the server request.
     *
     * @param authToken      Authorization token.
     * @param preferenceBody Preference info.
     */
    public void start ( String authToken, PreferenceBody preferenceBody ) {
        // User cannot modify the normal preference.
        if ( preferenceBody.getId() != 0 ) {
            TravlendarClient client = ServiceGenerator.createService( TravlendarClient.class, authToken );
            Call < Preference > call = client.modifyPreference( preferenceBody );
            call.enqueue( this );
        } else {
            // User is trying to modify the normal preference.
            Message msg = handler.obtainMessage( 1 );
            msg.sendToTarget();
        }
    }

    @Override
    public void onResponse ( Call < Preference > call, Response < Preference > response ) {
        Bundle bundle = new Bundle();
        if ( response.isSuccessful() ) {
            String jsonPreference = new Gson().toJson( response.body() );
            bundle.putString( "jsonPreference", jsonPreference );
        } else {
            Log.d( "ERROR_RESPONSE", response.toString() );
        }
        Message msg = handler.obtainMessage( response.code() );
        msg.setData( bundle );
        msg.sendToTarget();
    }

    @Override
    public void onFailure ( Call < Preference > call, Throwable t ) {
        Log.d( "INTERNET_CONNECTION", "ABSENT" );
        Message msg = handler.obtainMessage( 0 );
        msg.sendToTarget();
    }
}
