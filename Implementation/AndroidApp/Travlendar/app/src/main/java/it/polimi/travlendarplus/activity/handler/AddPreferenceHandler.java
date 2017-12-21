package it.polimi.travlendarplus.activity.handler;


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

/**
 * Handler that handles the server response to the preference addition.
 * It is used by the PreferencesActivity.
 */
public class AddPreferenceHandler extends Handler {

    private Context context;
    private PreferencesActivity preferencesActivity;

    public AddPreferenceHandler(Looper looper, Context context, PreferencesActivity preferencesActivity) {
        super(looper);
        this.context = context;
        this.preferencesActivity = preferencesActivity;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 0:
                Toast.makeText(context, "No internet connection available!", Toast.LENGTH_LONG).show();
                break;
            case 200:
                Toast.makeText(context, "Preference added!", Toast.LENGTH_LONG).show();
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String jsonPreference = bundle.getString("jsonPreference");
                Preference preference = new Gson()
                        .fromJson(jsonPreference, Preference.class);
                preferencesActivity.getPreferencesMap().put(preference.getName(), preference);
                preferencesActivity.populatePreferencesSpinner();
                break;
            case 400:
                Toast.makeText(context, "Invalid fields sent to server!", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                Log.d("ERROR_RESPONSE", msg.toString());
                break;
        }
        preferencesActivity.resumeNormalMode();
    }
}
