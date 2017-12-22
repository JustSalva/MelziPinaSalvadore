package it.polimi.travlendarplus.activity.handler.location;


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
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

/**
 * Handler that handles the server response to the locations request.
 * It is used by the EventEditorActivity.
 */
public class GetEventLocationsHandler extends DefaultHandler {

    private EventEditorActivity eventEditorActivity;

    public GetEventLocationsHandler(Looper looper, Context context, EventEditorActivity eventEditorActivity) {
        super(looper, context);
        this.eventEditorActivity = eventEditorActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what) {
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
                break;
        }
        eventEditorActivity.resumeNormalMode();
    }
}
