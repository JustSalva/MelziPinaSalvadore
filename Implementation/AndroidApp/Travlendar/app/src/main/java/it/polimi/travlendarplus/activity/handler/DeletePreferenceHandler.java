package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.PreferencesActivity;

/**
 * Handler that handles the server response to the preference deletion.
 * It is used by the PreferencesActivity.
 */
public class DeletePreferenceHandler extends Handler {

    private Context context;
    private PreferencesActivity preferencesActivity;

    public DeletePreferenceHandler(Looper looper, Context context, PreferencesActivity preferencesActivity) {
        super(looper);
        this.context = context;
        this.preferencesActivity = preferencesActivity;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 0:
                Toast.makeText(context, "No internet connection available!", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(context, "The normal type of event cannot be deleted!", Toast.LENGTH_LONG).show();
                break;
            case 200:
                // Notify the user that the preference has been removed.
                Toast.makeText(context, "Preference removed!", Toast.LENGTH_LONG).show();
                // Remove preference from the list.
                preferencesActivity.getPreferencesMap().remove(preferencesActivity.getSelectedPreference().getName());
                break;
            case 400:
                Toast.makeText(context, "The specified profile does not exist!", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                Log.d("ERROR_RESPONSE", msg.toString());
                break;
        }
        preferencesActivity.resumeNormalMode();
    }
}
