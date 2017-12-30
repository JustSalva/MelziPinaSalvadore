package it.polimi.travlendarplus.activity.handler;


import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.LoginActivity;
import it.polimi.travlendarplus.activity.RegistrationActivity;
import it.polimi.travlendarplus.activity.tasks.InsertUserTask;
import it.polimi.travlendarplus.activity.tasks.RemoveUserTask;
import it.polimi.travlendarplus.database.entity.User;

/**
 * Handler that handles the server response to the registration request.
 * It is used by the RegistrationActivity.
 */
public class RegistrationHandler extends DefaultHandler < RegistrationActivity > {

    //private RegistrationActivity registrationActivity;

    public RegistrationHandler ( Looper looper, RegistrationActivity registrationActivity ) {
        super( looper, registrationActivity );
        //this.registrationActivity = registrationActivity;
    }

    @Override
    public void handleMessage ( Message msg ) {
        switch ( msg.what ) {
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String token = bundle.getString( "token" );
                Log.d( "UNICODE", token );
                // Insert new User into the local DB.
                String email = activity.getEmail();
                String name = activity.getName();
                String surname = activity.getSurname();
                User user = new User( email, name, surname, token );
                Log.d( "INSERT_USER", user.toString() );
                new InsertUserTask( activity ).execute( user );
                //activity.startActivity(new Intent(activity, CalendarActivity.class));
                break;
            case 401:
                Toast.makeText( activity, "This email is already registered",
                        Toast.LENGTH_LONG ).show();
                break;
            case 408:
                Toast.makeText( activity, "Registration timeout expired",
                        Toast.LENGTH_LONG ).show();
                break;
            default:
                super.handleMessage( msg );
        }
        activity.resumeNormalMode();
    }
}
