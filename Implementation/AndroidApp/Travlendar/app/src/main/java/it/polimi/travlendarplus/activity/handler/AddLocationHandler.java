package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.Position;
import it.polimi.travlendarplus.activity.AccountActivity;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;

/**
 * Handler that handles the server response to the location addition.
 * It is used by the AccountActivity.
 */
public class AddLocationHandler extends Handler {

    private Context context;
    private AccountActivity accountActivity;

    public AddLocationHandler(Looper looper, Context context, AccountActivity accountActivity) {
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
                // Notify the user that the location has been added.
                Toast.makeText(context, "Location added!", Toast.LENGTH_LONG).show();
                // Add location to the list.
                Location location = new Location(
                        accountActivity.getLocationName(),
                        new Position(accountActivity.getLocationAddress()));
                accountActivity.getLocationsMap().put(accountActivity.getLocationName(), location);
                accountActivity.populateLocationsSpinner();
                break;
            case 400:
                // Shows the user which invalid fields have been sent to server.
                Toast.makeText(context, "Invalid fields sent to server!", Toast.LENGTH_LONG).show();
                ErrorResponse errorResponse = new Gson()
                        .fromJson(msg.getData().getString("errorResponse"), ErrorResponse.class);
                // Shows a toast for each error message.
                for (String message : errorResponse.getMessages()) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                break;
        }
        accountActivity.resumeNormalMode();
    }
}
