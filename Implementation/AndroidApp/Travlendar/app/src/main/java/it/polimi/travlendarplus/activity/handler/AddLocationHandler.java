package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.Position;
import it.polimi.travlendarplus.activity.AccountActivity;

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
                Toast.makeText(context, "Invalid fields sent to server!", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                break;
        }
        accountActivity.resumeNormalMode();
    }
}
