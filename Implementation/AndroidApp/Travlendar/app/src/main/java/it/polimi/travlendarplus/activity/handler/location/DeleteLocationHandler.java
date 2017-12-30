package it.polimi.travlendarplus.activity.handler.location;


import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.AccountActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

/**
 * Handler that handles the server response to the location deletion.
 * It is used by the AccountActivity.
 */
public class DeleteLocationHandler extends DefaultHandler < AccountActivity > {

    //private AccountActivity accountActivity;

    public DeleteLocationHandler ( Looper looper, AccountActivity accountActivity ) {
        super( looper, accountActivity );
        //this.accountActivity = accountActivity;
    }

    @Override
    public void handleMessage ( Message msg ) {
        super.handleMessage( msg );
        switch ( msg.what ) {
            case 200:
                // Notify the user that the location has been removed.
                Toast.makeText( activity, "Location removed!", Toast.LENGTH_LONG ).show();
                // Remove location to the list.
                activity.getLocationsMap()
                        .remove( activity.getSelectedLocation().getName() );
                activity.populateLocationsSpinner();
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
