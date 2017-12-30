package it.polimi.travlendarplus.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.fragment.DatePickerFragment;
import it.polimi.travlendarplus.activity.handler.event.DeleteEventHandler;
import it.polimi.travlendarplus.activity.handler.event.GetEventsHandler;
import it.polimi.travlendarplus.activity.handler.event.ScheduleEventHandler;
import it.polimi.travlendarplus.activity.listener.DragToDeleteEventListener;
import it.polimi.travlendarplus.activity.listener.DragToInfoListener;
import it.polimi.travlendarplus.activity.listener.DragToScheduleListener;
import it.polimi.travlendarplus.activity.listener.MyTouchEventListener;
import it.polimi.travlendarplus.database.entity.TravelComponent;
import it.polimi.travlendarplus.database.entity.User;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.database.view_model.CalendarViewModel;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.event.DeleteEventController;
import it.polimi.travlendarplus.retrofit.controller.event.GetEventsController;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Activity that displays the events in the database.
 * Allows the user to schedule and delete events by dragging them.
 */
public class CalendarActivity extends MenuActivity {
    // UI references,
    private TextView date_textView;
    private RelativeLayout hours_relativeLayout;
    private RelativeLayout events_relativeLayout;
    private RelativeLayout overlappingEvents_relativeLayout;

    private CalendarViewModel calendarViewModel;
    private UserViewModel userViewModel;
    private User user;
    // Variable to check if the events have already been downloaded.
    private boolean eventsDownloaded = false;
    private GenericEvent focusedEvent;

    private Calendar calendar = new GregorianCalendar( TimeZone.getTimeZone( "UTC" ) );
    private List < GenericEvent > eventsList = new ArrayList <>();
    private List < TravelComponent > travelComponentsList = new ArrayList <>();
    // Handlers for server responses.
    private Handler getEventsHandler;
    private Handler deleteEventHandler;
    private Handler scheduleEventHandler;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_calendar );
        super.setupMenuToolbar();

        // Saves UI references.
        date_textView = findViewById( R.id.date_textView );
        hours_relativeLayout = findViewById( R.id.hours_relativeLayout );
        fillHoursRelativeLayout();
        events_relativeLayout = findViewById( R.id.events_relativeLayout );
        overlappingEvents_relativeLayout = findViewById( R.id.overlappingEvents_relativeLayout );
        findViewById( R.id.addEvent_button ).setOnClickListener(
                click -> startActivity( new Intent( this, EventEditorActivity.class ) )
        );

        // Observe token and timestamp values.
        userViewModel = ViewModelProviders.of( this ).get( UserViewModel.class );
        userViewModel.getUser().observe( this, user -> {
            this.user = user;
            // To be called only on the first onCreate().
            if ( !eventsDownloaded && user.getToken()!= null) {
                eventsDownloaded = true;
                loadEventsFromServer();
            }
        } );

        // Observe events available in the selected date.
        calendarViewModel = ViewModelProviders.of( this ).get( CalendarViewModel.class );
        calendarViewModel.getAllEvents().observe(
                this, events -> {
                    eventsList = events;
                    fillEventsRelativeLayout();
                } );
        calendarViewModel.getAllTravelComponents().observe(
                this, travelComponents -> {
                    travelComponentsList = travelComponents;
                    fillEventsRelativeLayout();
                }
        );

        // Set handlers.
        getEventsHandler = new GetEventsHandler( Looper.getMainLooper(), this );
        deleteEventHandler = new DeleteEventHandler( Looper.getMainLooper(), this );
        scheduleEventHandler = new ScheduleEventHandler( Looper.getMainLooper(), this );

        date_textView.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged ( CharSequence charSequence, int i, int i1, int i2 ) {
                // Nothing happens.
            }

            @Override
            public void onTextChanged ( CharSequence charSequence, int i, int i1, int i2 ) {
                // Nothing happens.
            }

            @Override
            public void afterTextChanged ( Editable editable ) {
                // Update calendar.
                calendar = DateUtility.getCalendarFromString( date_textView.getText().toString() );
                // Display them.
                fillEventsRelativeLayout();
            }
        } );

        // Check if the date is already set.
        if ( date_textView.getText().length() == 0 ) {
            // Set today's date as date.
            calendar = new GregorianCalendar( TimeZone.getTimeZone( "UTC" ) );
            String dateString = DateUtility.getStringFromCalendar( calendar );
            date_textView.setText( dateString );
        }

        // Set info textView as drop recipient for drag action to see event info.
        findViewById( R.id.info_textView )
                .setOnDragListener( new DragToInfoListener( CalendarActivity.this ) );
        // Set events relativeLayout as drop recipient for drag action to schedule.
        findViewById( R.id.schedule_textView )
                .setOnDragListener( new DragToScheduleListener( getApplicationContext(), this ) );
        // Set date textView as drop recipient for drag action to delete.
        findViewById( R.id.delete_textView )
                .setOnDragListener( new DragToDeleteEventListener( this ) );
    }

    /**
     * Downloads and saves in the DB new events.
     */
    private void loadEventsFromServer () {
        // Send request to server.
        waitForServerResponse();
        GetEventsController getEventsController = new GetEventsController( getEventsHandler );
        getEventsController.start( user.getToken(), user.getTimestamp() );
    }

    /**
     * Sends request to delete an event to the server.
     */
    public void deleteEvent ( int eventId ) {
        // Send request to server.
        waitForServerResponse();
        DeleteEventController deleteEventController = new DeleteEventController( deleteEventHandler );
        deleteEventController.start( user.getToken(), eventId );
    }

    /**
     * Inserts 24 hours textViews indicators in the hours_relativeLayout.
     */
    private void fillHoursRelativeLayout () {
        hours_relativeLayout.removeAllViews();
        for ( int i = 0; i < 24; i++ ) {
            TextView textView = new TextView( getApplicationContext() );
            textView.setText( Integer.toString( i ) );
            textView.setTextColor( Color.parseColor( "#000000" ) );
            textView.setTypeface( null, Typeface.BOLD );
            textView.setTextAlignment( View.TEXT_ALIGNMENT_CENTER );
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( MATCH_PARENT, 200 );
            params.setMargins( 0, i * 60 * 2, 0, 0 );
            textView.setLayoutParams( params );
            hours_relativeLayout.addView( textView );
        }
    }

    /**
     * Fills relative layouts with events, checking if they are scheduled or not.
     * Breaks are inserted before regular events to make sure that the latter are on top.
     */
    private void fillEventsRelativeLayout () {
        // Clears the relative layouts of the past views.
        events_relativeLayout.removeAllViews();
        overlappingEvents_relativeLayout.removeAllViews();
        // Get long representing selected date.
        long selectedDate = ( calendar.getTimeInMillis() / 1000 ) +
                ( TimeZone.getDefault().getOffset( calendar.getTime().getTime() ) / 1000 );
        // Inserts break events.
        for ( GenericEvent event : eventsList ) {
            if ( event.getType().equals( GenericEvent.EventType.BREAK ) && ( selectedDate == event.getDate() ) ) {
                if ( event.isScheduled() ) {
                    // Insert scheduled breaks.
                    events_relativeLayout.addView( createEventTextView( event ) );
                } else {
                    // Insert overlapping breaks.
                    overlappingEvents_relativeLayout.addView( createEventTextView( event ) );
                }
            }
        }
        // Inserts regular events.
        for ( GenericEvent event : eventsList ) {
            if ( event.getType().equals( GenericEvent.EventType.EVENT ) && ( selectedDate == event.getDate() ) ) {
                if ( event.isScheduled() ) {
                    // Insert scheduled events.
                    events_relativeLayout.addView( createEventTextView( event ) );
                    // Inserts travels for scheduled events.
                    events_relativeLayout.addView( createTravelTextView( event ) );
                } else {
                    // Insert overlapping events.
                    overlappingEvents_relativeLayout.addView( createEventTextView( event ) );
                }
            }
        }
    }

    /**
     * Creates and return a textView containing the event information.
     *
     * @param event The event to be displayed in the textView.
     * @return A textView containing the event info.
     */
    private TextView createEventTextView ( GenericEvent event ) {
        TextView textView = new TextView( getApplicationContext() );
        textView.setText( event.getName() );
        textView.setTextColor( Color.parseColor( "#000000" ) );
        textView.setTextAlignment( View.TEXT_ALIGNMENT_CENTER );
        textView.setTypeface( null, Typeface.BOLD );
        textView.setId( ( int ) event.getId() );
        textView = setColorTextView( textView, event );
        textView = setDimensionTextView( textView, event );
        // Add on touch listener to events.
        textView.setOnTouchListener( new MyTouchEventListener( CalendarActivity.this, event ) );
        return textView;
    }

    /**
     * Creates and return a textView containing the travel information.
     *
     * @param event The event related to the travel to be displayed in the textView.
     * @return A textView containing the travel info.
     */
    private TextView createTravelTextView ( GenericEvent event ) {
        List < TravelComponent > eventTravelComponents = new ArrayList <>();
        for ( TravelComponent travelComponent : travelComponentsList ) {
            if ( travelComponent.getEventId() == event.getId() ) {
                eventTravelComponents.add( travelComponent );
            }
        }
        // Saves starting and ending travel time.
        long startingTime = Long.MAX_VALUE;
        long endingTime = 0;
        for ( TravelComponent travelComponent : eventTravelComponents ) {
            if ( travelComponent.getEndTime() > endingTime ) {
                endingTime = travelComponent.getEndTime();
            }
            if ( travelComponent.getStartTime() < startingTime ) {
                startingTime = travelComponent.getStartTime();
            }
        }
        // Draw travel only in current date.
        if ( startingTime < event.getDate() ) {
            startingTime = event.getDate();
        }
        // Set style.
        TextView textView = new TextView( getApplicationContext() );
        textView.setText( event.getName().concat( "'s travel" ) );
        textView.setTextColor( Color.parseColor( "#000000" ) );
        textView.setTextAlignment( View.TEXT_ALIGNMENT_CENTER );
        textView.setTypeface( null, Typeface.BOLD );
        // Set colors.
        textView.setBackground( getResources().getDrawable( R.drawable.rectangle, getTheme() ) );
        textView.setBackgroundColor( Color.parseColor( "#ADFF2F" ) );
        textView = setDimensionTextView( textView, event );
        // Set the height depending on the event duration.
        int minDuration = ( int ) ( endingTime - startingTime ) / 60;
        int height = minDuration * 2;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( MATCH_PARENT, height );
        // Sets the top margin depending on the starting time of the event.
        long minutesOfDay = (
                ( startingTime + TimeZone.getDefault().getOffset( calendar.getTime().getTime() ) / 1000 )
                        % 86400 )
                / 60;
        int marginTop = ( int ) ( minutesOfDay * 2 );
        // If travel spans on two days, draw it right.
        if ( startingTime == event.getDate() ) {
            marginTop = 0;
            params.height = ( int ) ( endingTime - startingTime +
                    TimeZone.getDefault().getOffset( calendar.getTime().getTime() ) / 1000 ) / 30;
        }
        params.setMargins( 30, marginTop, 30, 10 );
        textView.setLayoutParams( params );
        // Setup onClickListener to see travel components.
        textView.setOnClickListener( click -> {
            Intent intent = new Intent( CalendarActivity.this, TravelTicketActivity.class )
                    .putExtra( "EVENT_ID", event.getId() );
            startActivity( intent );
        } );
        return textView;
    }

    /**
     * Sets the right colors for the event textView, depending on scheduling and type of event.
     *
     * @param textView TextView to be modified.
     * @param event    Event linked to the TextView.
     * @return Modified textView.
     */
    private TextView setColorTextView ( TextView textView, GenericEvent event ) {
        textView.setBackground( getResources().getDrawable( R.drawable.rectangle, getTheme() ) );
        int color = 0;
        // Check type of event.
        if ( event.getType().equals( GenericEvent.EventType.EVENT ) ) {
            if ( event.isScheduled() ) {
                // Sets the background color green.
                color = Color.parseColor( "#FF88CF92" );
            } else {
                // Sets the background color red.
                color = Color.parseColor( "#FF0000" );
            }
        } else if ( event.getType().equals( GenericEvent.EventType.BREAK ) ) {
            if ( event.isScheduled() ) {
                // Set the background color yellow.
                color = Color.parseColor( "#FFFF00" );
            } else {
                // Set the background color orange.
                color = Color.parseColor( "#FFA500" );
            }
        }
        textView.setBackgroundColor( color );
        return textView;
    }

    /**
     * Sets the right dimension for the event textView, depending on duration and type of event.
     *
     * @param textView TextView to be modified.
     * @param event    Event linked to the TextView.
     * @return Modified textView.
     */
    private TextView setDimensionTextView ( TextView textView, GenericEvent event ) {
        // Set the height depending on the event duration.
        int minDuration = ( int ) ( event.getEndTime() - event.getStartTime() ) / 60;
        int height = minDuration * 2;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( MATCH_PARENT, height );
        // Sets the top margin depending on the starting time of the event, considering time zones.
        long minutesOfDay = (
                ( event.getStartTime() + TimeZone.getDefault().getOffset( calendar.getTime().getTime() ) / 1000 )
                        % 86400 )
                / 60;
        int marginTop = ( int ) ( minutesOfDay * 2 );
        // Set margins depending on the type of event.
        int marginSide = event.getType() == GenericEvent.EventType.EVENT ? 30 : 10;
        params.setMargins( marginSide, marginTop, marginSide, 10 );
        textView.setLayoutParams( params );
        return textView;
    }

    /**
     * Shows a datePicker fragment to select a date.
     */
    public void showDatePickerDialog ( View view ) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTextView( findViewById( R.id.date_textView ) );
        newFragment.show( getFragmentManager(), "datePicker" );
        loadEventsFromServer();
    }

    public String getToken () {
        return user.getToken();
    }

    public Handler getDeleteEventHandler () {
        return deleteEventHandler;
    }

    public Handler getScheduleEventHandler () {
        return scheduleEventHandler;
    }

    public GenericEvent getFocusedEvent () {
        return focusedEvent;
    }

    public void setFocusedEvent ( GenericEvent focusedEvent ) {
        this.focusedEvent = focusedEvent;
    }

    public void setInfoDeleteTVVisibility ( int visibility ) {
        findViewById( R.id.info_textView ).setVisibility( visibility );
        findViewById( R.id.delete_textView ).setVisibility( visibility );
    }

    @Override
    public void onBackPressed () {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        if ( drawer.isDrawerOpen( GravityCompat.START ) ) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
            startActivity( new Intent( this, CalendarActivity.class ) );
        }
    }
}
