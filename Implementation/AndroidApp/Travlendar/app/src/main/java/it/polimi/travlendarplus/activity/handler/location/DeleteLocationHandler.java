package it.polimi.travlendarplus.activity.handler.location;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.AccountActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

/**
 * Handler that handles the server response to the location deletion.
 * It is used by the AccountActivity.
 */
public class DeleteLocationHandler extends DefaultHandler {

    private AccountActivity accountActivity;

    public DeleteLocationHandler(Looper looper, Context context, AccountActivity accountActivity) {
        super(looper, context);
        this.accountActivity = accountActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                // Notify the user that the location has been removed.
                Toast.makeText(context, "Location removed!", Toast.LENGTH_LONG).show();
                // Remove location to the list.
                accountActivity.getLocationsMap()
                        .remove(accountActivity.getSelectedLocation().getName());
                accountActivity.populateLocationsSpinner();
                break;
            default:
                break;
        }
        accountActivity.resumeNormalMode();
    }
}
