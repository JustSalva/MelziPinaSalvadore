package it.polimi.travlendarplus.activity.handler.location;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.Position;
import it.polimi.travlendarplus.activity.AccountActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;

/**
 * Handler that handles the server response to the location addition.
 * It is used by the AccountActivity.
 */
public class AddLocationHandler extends DefaultHandler {

    private AccountActivity accountActivity;

    public AddLocationHandler(Looper looper, Context context, AccountActivity accountActivity) {
        super(looper, context);
        this.accountActivity = accountActivity;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 200:
                // Notify the user that the location has been added.
                Toast.makeText(context, "Location added!", Toast.LENGTH_LONG).show();
                // Add location to the list.
                Location location = new Location(
                        accountActivity.getLocationName(),
                        new Position(accountActivity.getLocationAddress()));
                accountActivity.getLocationsMap().put(accountActivity.getLocationName(), location);
                accountActivity.populateLocationsSpinner();
                break;
            case 500:
                Toast.makeText(context, "You already have a location with that name!", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Error: " + Integer.toString(msg.what), Toast.LENGTH_LONG).show();
                Log.d("ERROR", Integer.toString(msg.what));
                break;
        }
        accountActivity.resumeNormalMode();
    }
}
