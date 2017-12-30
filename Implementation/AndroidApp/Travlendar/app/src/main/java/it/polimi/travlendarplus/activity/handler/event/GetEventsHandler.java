package it.polimi.travlendarplus.activity.handler.event;


import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.LoginActivity;
import it.polimi.travlendarplus.activity.handler.DefaultHandler;
import it.polimi.travlendarplus.activity.tasks.InsertBreakEventsTask;
import it.polimi.travlendarplus.activity.tasks.InsertEventsTask;
import it.polimi.travlendarplus.activity.tasks.RemoveUserTask;
import it.polimi.travlendarplus.retrofit.response.event.BreakEventResponse;
import it.polimi.travlendarplus.retrofit.response.event.EventResponse;

/**
 * Handler that handles the server response to the events request.
 * It is used by the CalendarActivity.
 */
public class GetEventsHandler extends DefaultHandler < CalendarActivity > {

    //private CalendarActivity calendarActivity;

    public GetEventsHandler ( Looper looper, CalendarActivity calendarActivity ) {
        super( looper, calendarActivity );
        //this.calendarActivity = calendarActivity;
    }

    @Override
    public void handleMessage ( Message msg ) {
        switch ( msg.what ) {
            case 0:
                Toast.makeText( activity, "No internet connection available!",
                        Toast.LENGTH_LONG ).show();
                break;
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                Log.d( "EVENTS_JSON", bundle.getString( "jsonEvents" ) );
                // Save events from JSON.
                String jsonEvents = bundle.getString( "jsonEvents" );
                List < EventResponse > events = new Gson().fromJson(
                        jsonEvents,
                        new TypeToken < List < EventResponse > >() {
                        }.getType()
                );
                Log.d( "BREAK_EVENTS_JSON", bundle.getString( "jsonBreakEvents" ) );
                // Save break events from JSON.
                String jsonBreakEvents = bundle.getString( "jsonBreakEvents" );

                List < BreakEventResponse > breakEvents = new Gson().fromJson(
                        jsonBreakEvents,
                        new TypeToken < List < BreakEventResponse > >() {
                        }.getType()
                );
                // If new events added, notify user.
                if ( !( ( events != null && events.isEmpty() ) &&
                        ( breakEvents != null && breakEvents.isEmpty() ) ) ) {
                    Toast.makeText( activity, "Events updated!", Toast.LENGTH_LONG ).show();
                }
                // Write new events into DB.
                new InsertEventsTask( activity, events ).execute();
                // Write new break events into DB.
                new InsertBreakEventsTask( activity, breakEvents ).execute();
                break;
            case 401:
                Toast.makeText( activity, "You logged in from another device!",
                        Toast.LENGTH_LONG ).show();
                new RemoveUserTask( activity.getApplicationContext() ).execute();
                activity.startActivity( new Intent( activity, LoginActivity.class ) );
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
