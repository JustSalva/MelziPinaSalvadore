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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.activity.EventEditorActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

/**
 * Handler that handles the server response to the preferences request.
 * It is used by the EventEditorActivity.
 */
public class GetEventPreferencesHandler extends DefaultHandler {

    private EventEditorActivity eventEditorActivity;

    public GetEventPreferencesHandler(Looper looper, Context context, EventEditorActivity eventEditorActivity) {
        super(looper, context);
        this.eventEditorActivity = eventEditorActivity;
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
                Map<String, Preference> preferencesMap = new HashMap<>();
                preferencesMap.put("Normal", new Preference());
                eventEditorActivity.setPreferencesMap(preferencesMap);
                for (Preference preference : preferences) {
                    eventEditorActivity.getPreferencesMap().put(preference.getName(), preference);
                }
                // Update preferences spinner.
                eventEditorActivity.populatePreferencesSpinner();
                break;
            default:
                break;
        }
        eventEditorActivity.resumeNormalMode();
    }
}
