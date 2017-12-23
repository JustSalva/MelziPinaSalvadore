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
public class DeletePreferenceHandler extends DefaultHandler {

    private PreferencesActivity preferencesActivity;

    public DeletePreferenceHandler(Looper looper, Context context, PreferencesActivity preferencesActivity) {
        super(looper, context);
        this.preferencesActivity = preferencesActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 1:
                Toast.makeText(context, "The normal type of event cannot be deleted!", Toast.LENGTH_LONG).show();
                break;
            case 200:
                // Notify the user that the preference has been removed.
                Toast.makeText(context, "Preference removed!", Toast.LENGTH_LONG).show();
                // Remove preference from the list.
                preferencesActivity.getPreferencesMap().remove(preferencesActivity.getSelectedPreference().getName());
                break;
            default:
                break;
        }
        preferencesActivity.resumeNormalMode();
    }
}
