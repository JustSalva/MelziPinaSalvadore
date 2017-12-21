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

import java.util.HashMap;
import java.util.List;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.activity.EventEditorActivity;

/**
 * Handler that handles the server response to the locations request.
 * It is used by the EventEditorActivity.
 */
public class GetEventLocationsHandler extends Handler {

    private Context context;
    private EventEditorActivity eventEditorActivity;

    public GetEventLocationsHandler(Looper looper, Context context, EventEditorActivity eventEditorActivity) {
        super(looper);
        this.context = context;
        this.eventEditorActivity = eventEditorActivity;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 0:
                Toast.makeText(context, "No internet connection available!", Toast.LENGTH_LONG).show();
                break;
            case 200:
                Toast.makeText(context, "Locations updated!", Toast.LENGTH_LONG).show();
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String jsonLocations = bundle.getString("jsonLocations");
                List<Location> locations = new Gson().fromJson(jsonLocations, new TypeToken<List<Location>>(){}.getType());
                eventEditorActivity.setLocationsMap(new HashMap<>());
                for (Location location : locations) {
                    eventEditorActivity.getLocationsMap().put(location.getName(), location);
                }
                eventEditorActivity.populateLocationsSpinner();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                Log.d("ERROR_RESPONSE", msg.toString());
                break;
        }
        eventEditorActivity.resumeNormalMode();
    }
}
