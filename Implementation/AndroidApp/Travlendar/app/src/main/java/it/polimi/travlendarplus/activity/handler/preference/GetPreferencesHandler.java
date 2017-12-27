package it.polimi.travlendarplus.activity.handler.preference;


import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.activity.EventEditorActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.activity.handler.PreferenceLoader;

/**
 * Handler that handles the server response to the preferences request.
 * It is used by the EventEditorActivity.
 */
public class GetPreferencesHandler extends DefaultHandler {

    private PreferenceLoader activity;

    public GetPreferencesHandler(Looper looper, Context context, PreferenceLoader activity) {
        super(looper, context);
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String jsonPreferences = bundle.getString("jsonPreferences");
                List<Preference> preferences = new Gson()
                        .fromJson(
                                jsonPreferences,
                                new TypeToken<List<Preference>>(){}.getType()
                        );
                // Fill map of preferences.
                Map<String, Preference> preferencesMap = new HashMap<>();
                preferencesMap.put("Normal", new Preference());
                for (Preference preference : preferences) {
                    preferencesMap.put(preference.getName(), preference);
                }
                // Update preferences spinner.
                activity.updatePreferences(preferencesMap);
                break;
            default:
                break;
        }
    }
}
