package it.polimi.travlendarplus.activity.handler.event;

import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.EventEditorActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;


/**
 * Handler that handles the server response to the event addition.
 * It is used by the EventEditorActivity.
 */
public class AddEventHandler extends DefaultHandler < EventEditorActivity > {

    //private EventEditorActivity eventEditorActivity;

    public AddEventHandler ( Looper looper, EventEditorActivity eventEditorActivity ) {
        super( looper, eventEditorActivity );
        //this.eventEditorActivity = eventEditorActivity;
    }

    @Override
    public void handleMessage ( Message msg ) {
        super.handleMessage( msg );
        switch ( msg.what ) {
            case 200:
                // Notify the user that the event has been added.
                Toast.makeText( activity, "Event added!", Toast.LENGTH_LONG ).show();
                activity.startActivity( new Intent( activity, CalendarActivity.class ) );
                break;
            case 503:
                Toast.makeText( activity, "Google Maps not reachable!", Toast.LENGTH_LONG ).show();
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
