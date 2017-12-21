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
import it.polimi.travlendarplus.activity.AccountActivity;

/**
 * Handler that handles the server response to the locations request.
 * It is used by the AccountActivity.
 */
public class GetAccountLocationsHandler extends Handler {

    private Context context;
    private AccountActivity accountActivity;

    public GetAccountLocationsHandler(Looper looper, Context context, AccountActivity accountActivity) {
        super(looper);
        this.context = context;
        this.accountActivity = accountActivity;
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
                accountActivity.setLocationsMap(new HashMap<>());
                for (Location location : locations) {
                    accountActivity.getLocationsMap().put(location.getName(), location);
                }
                accountActivity.populateLocationsSpinner();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                Log.d("ERROR_RESPONSE", msg.toString());
                break;
        }
        accountActivity.resumeNormalMode();
    }
}
