package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.activity.PreferencesActivity;

/**
 * Handler that handles the server response to the preferences request.
 * It is used by the PreferencesActivity.
 */
public class GetPreferencesHandler extends Handler {

    private Context context;
    private PreferencesActivity preferencesActivity;

    public GetPreferencesHandler(Looper looper, Context context, PreferencesActivity preferencesActivity) {
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
                Toast.makeText(context, "Preferences updated!", Toast.LENGTH_LONG).show();
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String jsonPreferences = bundle.getString("jsonPreferences");
                List<Preference> preferences = new Gson()
                        .fromJson(
                                jsonPreferences,
                                new TypeToken<List<Preference>>(){}.getType()
                        );
                // Fill map of preferences.
                for (Preference preference : preferences) {
                    preferencesActivity.getPreferencesMap().put(preference.getName(), preference);
                }
                // Update preferences spinner.
                preferencesActivity.populatePreferencesSpinner();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                Log.d("ERROR_RESPONSE", msg.toString());
                break;
        }
        preferencesActivity.resumeNormalMode();
    }
}
