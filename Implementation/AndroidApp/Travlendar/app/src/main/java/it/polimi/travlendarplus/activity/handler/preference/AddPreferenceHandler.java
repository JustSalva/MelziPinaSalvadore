package it.polimi.travlendarplus.activity.handler.preference;


import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;

import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.activity.PreferencesActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

/**
 * Handler that handles the server response to the preference addition.
 * It is used by the PreferencesActivity.
 */
public class AddPreferenceHandler extends DefaultHandler < PreferencesActivity > {

    //private PreferencesActivity preferencesActivity;

    public AddPreferenceHandler ( Looper looper, PreferencesActivity preferencesActivity ) {
        super( looper, preferencesActivity );
        //this.context = context;
        //this.preferencesActivity = preferencesActivity;
    }

    @Override
    public void handleMessage ( Message msg ) {
        super.handleMessage( msg );
        switch ( msg.what ) {
            case 200:
                Toast.makeText( activity, "Preference added!", Toast.LENGTH_LONG ).show();
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String jsonPreference = bundle.getString( "jsonPreference" );
                Preference preference = new Gson()
                        .fromJson( jsonPreference, Preference.class );
                activity.getPreferencesMap().put( preference.getName(), preference );
                activity.populatePreferencesSpinner();
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
