package it.polimi.travlendarplus.activity.handler.location;


import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.Position;
import it.polimi.travlendarplus.activity.AccountActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;

/**
 * Handler that handles the server response to the location addition.
 * It is used by the AccountActivity.
 */
public class AddLocationHandler extends DefaultHandler < AccountActivity > {

    //private AccountActivity accountActivity;

    public AddLocationHandler ( Looper looper, AccountActivity accountActivity ) {
        super( looper, accountActivity );
        //this.accountActivity = accountActivity;
    }

    @Override
    public void handleMessage ( Message msg ) {
        switch ( msg.what ) {
            case 200:
                // Notify the user that the location has been added.
                Toast.makeText( activity, "Location added!", Toast.LENGTH_LONG ).show();
                // Add location to the list.
                Location location = new Location(
                        activity.getLocationName().replace( " ", "_" ),
                        new Position( activity.getLocationAddress() ) );
                activity.getLocationsMap().put( activity.getLocationName()
                        .replace( " ", "_" ), location );
                activity.populateLocationsSpinner();
                break;
            case 500:
                Toast.makeText( activity, "You already have a location with that name!",
                        Toast.LENGTH_LONG ).show();
                break;
            default:
                Toast.makeText( activity, "Error: " + Integer.toString( msg.what ),
                        Toast.LENGTH_LONG ).show();
                Log.d( "ERROR", Integer.toString( msg.what ) );
                break;
        }
        activity.resumeNormalMode();
    }
}
