package it.polimi.travlendarplus.activity.handler.location;


import android.app.Activity;
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

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.activity.AccountActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.activity.handler.LocationLoader;

/**
 * Handler that handles the server response to the locations request.
 * It is used by the AccountActivity.
 */
public class GetLocationsHandler extends DefaultHandler<Activity> {

    //private LocationLoader activity;

    public GetLocationsHandler(Looper looper, LocationLoader activity) {
        super(looper, (Activity)activity);
        //this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what) {
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String jsonLocations = bundle.getString("jsonLocations");
                List<Location> locations =
                        new Gson().fromJson(jsonLocations, new TypeToken<List<Location>>(){}.getType());
                Map<String, Location> locationMap = new HashMap<>();
                for (Location location : locations) {
                    locationMap.put(location.getName(), location);
                }
                ((LocationLoader)activity).updateLocations(locationMap);
                break;
            default:
                break;
        }
    }
}
