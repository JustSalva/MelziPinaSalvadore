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
public class ModifyPreferenceHandler extends DefaultHandler {

    private PreferencesActivity preferencesActivity;

    public ModifyPreferenceHandler(Looper looper, Context context, PreferencesActivity preferencesActivity) {
        super(looper, context);
        this.preferencesActivity = preferencesActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 1:
                Toast.makeText(context, "The normal type of event cannot be modified!", Toast.LENGTH_LONG).show();
                break;
            case 200:
                Toast.makeText(context, "Preference edited!", Toast.LENGTH_LONG).show();
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String jsonPreference = bundle.getString("jsonPreference");
                Preference preference = new Gson()
                        .fromJson(jsonPreference, Preference.class);
                preferencesActivity.getPreferencesMap().put(preference.getName(), preference);
                preferencesActivity.populatePreferencesSpinner();
                break;
            default:
                break;
        }
        preferencesActivity.resumeNormalMode();
    }
}
