package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.AccountActivity;

public class DeleteLocationHandler extends Handler {

    private Context context;
    private AccountActivity accountActivity;

    public DeleteLocationHandler(Looper looper, Context context, AccountActivity accountActivity) {
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
                // Notify the user that the location has been removed.
                Toast.makeText(context, "Location removed!", Toast.LENGTH_LONG).show();
                // Remove location to the list.
                accountActivity.getLocationsMap()
                        .remove(accountActivity.getSelectedLocation().getName());
                accountActivity.populateLocationsSpinner();
                break;
            case 400:
                Toast.makeText(context, "The location specified does not exist!", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                break;
        }
        accountActivity.resumeNormalMode();
    }
}
