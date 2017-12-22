package it.polimi.travlendarplus.activity.handler.preference;


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
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

/**
 * Handler that handles the server response to the preferences request.
 * It is used by the PreferencesActivity.
 */
public class GetPreferencesHandler extends DefaultHandler {

    private PreferencesActivity preferencesActivity;

    public GetPreferencesHandler(Looper looper, Context context, PreferencesActivity preferencesActivity) {
        super(looper, context);
        this.preferencesActivity = preferencesActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
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
                break;
        }
        preferencesActivity.resumeNormalMode();
    }
}
