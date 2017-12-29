package it.polimi.travlendarplus.activity.handler.preference;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.PreferencesActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

/**
 * Handler that handles the server response to the preference deletion.
 * It is used by the PreferencesActivity.
 */
public class DeletePreferenceHandler extends DefaultHandler<PreferencesActivity> {

    //private PreferencesActivity preferencesActivity;

    public DeletePreferenceHandler(Looper looper, PreferencesActivity preferencesActivity) {
        super(looper, preferencesActivity);
        //this.preferencesActivity = preferencesActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 1:
                Toast.makeText(activity, "The normal type of event cannot be deleted!", Toast.LENGTH_LONG).show();
                break;
            case 200:
                // Notify the user that the preference has been removed.
                Toast.makeText(activity, "Preference removed!", Toast.LENGTH_LONG).show();
                // Remove preference from the list.
                activity.getPreferencesMap().remove(activity.getSelectedPreference().getName());
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
