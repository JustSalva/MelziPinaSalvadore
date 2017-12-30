package it.polimi.travlendarplus.retrofit.controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import it.polimi.travlendarplus.retrofit.ServiceGenerator;
import it.polimi.travlendarplus.retrofit.TravlendarClient;
import it.polimi.travlendarplus.retrofit.body.LoginBody;
import it.polimi.travlendarplus.retrofit.response.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller that performs a login request to the server.
 * Fills a message to be sent to the desired handler.
 */
public class LoginController implements Callback < LoginResponse > {

    private Handler handler;

    public LoginController ( Handler handler ) {
        this.handler = handler;
    }

    /**
     * Starts the server request.
     *
     * @param email    Email of the user.
     * @param password Password of the user.
     * @param idDevice Id of the device.
     */
    public void start ( String email, String password, String idDevice ) {
        TravlendarClient client = ServiceGenerator.createService( TravlendarClient.class );
        Call < LoginResponse > call = client.login( new LoginBody( email, password, idDevice ) );
        call.enqueue( this );
    }

    @Override
    public void onResponse ( Call < LoginResponse > call, Response < LoginResponse > response ) {
        Bundle bundle = new Bundle();
        if ( response.isSuccessful() ) {
            LoginResponse loginResponse = response.body();
            bundle.putString( "name", loginResponse.getName() );
            bundle.putString( "surname", loginResponse.getSurname() );
            bundle.putString( "token", loginResponse.getToken() );
        } else {
            Log.d( "ERROR_RESPONSE", response.toString() );
        }
        Message msg = handler.obtainMessage( response.code() );
        msg.setData( bundle );
        msg.sendToTarget();
    }

    @Override
    public void onFailure ( Call < LoginResponse > call, Throwable t ) {
        Log.d( "INTERNET_CONNECTION", "ABSENT" );
        Message msg = handler.obtainMessage( 0 );
        msg.sendToTarget();
    }
}
