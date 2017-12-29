package it.polimi.travlendarplus.activity.handler.preference;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.activity.PreferencesActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

/**
 * Handler that handles the server response to the preference modification.
 * It is used by the PreferencesActivity.
 */
public class ModifyPreferenceHandler extends DefaultHandler<PreferencesActivity> {

    //private PreferencesActivity preferencesActivity;

    public ModifyPreferenceHandler(Looper looper, PreferencesActivity preferencesActivity) {
        super(looper, preferencesActivity);
        //this.preferencesActivity = preferencesActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 1:
                Toast.makeText(activity, "The normal type of event cannot be modified!", Toast.LENGTH_LONG).show();
                break;
            case 200:
                Toast.makeText(activity, "Preference edited!", Toast.LENGTH_LONG).show();
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String jsonPreference = bundle.getString("jsonPreference");
                Preference preference = new Gson()
                        .fromJson(jsonPreference, Preference.class);
                activity.getPreferencesMap().put(preference.getName(), preference);
                activity.populatePreferencesSpinner();
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
