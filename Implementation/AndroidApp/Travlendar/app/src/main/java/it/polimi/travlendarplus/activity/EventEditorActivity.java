package it.polimi.travlendarplus.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.fragment.DatePickerFragment;
import it.polimi.travlendarplus.activity.fragment.TimePickerFragment;
import it.polimi.travlendarplus.activity.handler.LocationLoader;
import it.polimi.travlendarplus.activity.handler.PreferenceLoader;
import it.polimi.travlendarplus.activity.handler.event.AddEventHandler;
import it.polimi.travlendarplus.activity.handler.location.GetLocationsHandler;
import it.polimi.travlendarplus.activity.handler.preference.GetPreferencesHandler;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.body.BreakEventBody;
import it.polimi.travlendarplus.retrofit.body.EventBody;
import it.polimi.travlendarplus.retrofit.controller.event.AddEventController;
import it.polimi.travlendarplus.retrofit.controller.location.GetLocationsController;
import it.polimi.travlendarplus.retrofit.controller.preference.GetPreferencesController;

/**
 * Activity that allows the user to create a new event.
 * Event editing to be implemented.
 */
public class EventEditorActivity extends MenuActivity implements LocationLoader, PreferenceLoader {
    // UI references.
    private LinearLayout normalEvent_linearLayout;
    private LinearLayout breakEvent_linearLayout;
    // Text views.
    private AutoCompleteTextView eventName_editText;
    private TextView date_textView;
    private TextView startingTime_textView;
    private TextView endingTime_textView;
    private TextView minimumTime_textView;
    private AutoCompleteTextView description_editText;
    // Spinners.
    private Spinner typeOfEvent_spinner;
    private Spinner eventLocation_spinner;
    private Spinner startTravelingAt_spinner;
    private Spinner departureLocation_spinner;
    // Locations.
    private Map < String, Location > locationMap;
    private Location selectedEventLocation;
    private Location selectedDepartureLocation;
    // Preferences.
    private Map < String, Preference > preferenceMap;
    private Preference selectedPreference;
    // Booleans.
    private boolean startTravelingAtLast;
    // Handlers for server responses.
    private Handler getEventLocationsHandler;
    private Handler getEventPreferencesHandler;
    private Handler addEventHandler;

    private UserViewModel userViewModel;
    private String token;
    // Variable to check if the events have already been downloaded.
    private boolean dataDownloaded = false;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_event_editor );
        super.setupMenuToolbar();

        normalEvent_linearLayout = findViewById( R.id.normalEvent_linearLayout );
        breakEvent_linearLayout = findViewById( R.id.breakEvent_linearLayout );
        eventName_editText = findViewById( R.id.eventName_editText );
        date_textView = findViewById( R.id.date_textView );
        startingTime_textView = findViewById( R.id.startingTime_textView );
        endingTime_textView = findViewById( R.id.endingTime_textView );
        minimumTime_textView = findViewById( R.id.minimumTime_textView );
        description_editText = findViewById( R.id.description_editText );
        typeOfEvent_spinner = findViewById( R.id.typeofEvent_spinner );
        eventLocation_spinner = findViewById( R.id.eventLocation_spinner );
        startTravelingAt_spinner = findViewById( R.id.startTravelingAt_spinner );
        departureLocation_spinner = findViewById( R.id.departureLocation_spinner );

        // Observe token and timestamp values.
        userViewModel = ViewModelProviders.of( this ).get( UserViewModel.class );
        userViewModel.getUser().observe( this, user -> {
            token = user != null ? user.getToken() : "";
            // To be called only on the first onCreate().
            if ( !dataDownloaded ) {
                loadLocationsFromServer();
                loadPreferencesFromServer();
                populateStartTravelingAtSpinner();
                dataDownloaded = true;
            }
        } );

        // Setup type of event spinner listener.
        typeOfEvent_spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected ( AdapterView < ? > adapterView, View view, int i, long l ) {
                selectedPreference = preferenceMap.get( adapterView.getSelectedItem().toString() );
            }

            @Override
            public void onNothingSelected ( AdapterView < ? > adapterView ) {
                // Nothing happens.
            }
        } );
        // Setup event location spinner listener.
        eventLocation_spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected ( AdapterView < ? > adapterView, View view, int i, long l ) {
                selectedEventLocation = locationMap.get( adapterView.getSelectedItem().toString() );
            }

            @Override
            public void onNothingSelected ( AdapterView < ? > adapterView ) {
                // Nothing happens.
            }
        } );
        // Setup previous location spinner listener.
        departureLocation_spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected ( AdapterView < ? > adapterView, View view, int i, long l ) {
                selectedDepartureLocation = locationMap.get( adapterView.getSelectedItem().toString() );
            }

            @Override
            public void onNothingSelected ( AdapterView < ? > adapterView ) {
                // Nothing happens.
            }
        } );
        // Setup start traveling at spinner listener;
        startTravelingAt_spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected ( AdapterView < ? > adapterView, View view, int i, long l ) {
                startTravelingAtLast = adapterView.getSelectedItem().toString().equals( "Latest" );
            }

            @Override
            public void onNothingSelected ( AdapterView < ? > adapterView ) {
                // Nothing happens.
            }
        } );
        getEventLocationsHandler =
                new GetLocationsHandler( Looper.getMainLooper(), this );
        getEventPreferencesHandler =
                new GetPreferencesHandler( Looper.getMainLooper(), this );
        addEventHandler =
                new AddEventHandler( Looper.getMainLooper(), this );

        findViewById( R.id.confirm_button ).setOnClickListener( click -> sendEventToServer() );
    }

    private void sendEventToServer () {
        if ( !validate() ) {
            Toast.makeText( getBaseContext(), "Something is wrong", Toast.LENGTH_LONG ).show();
            return;
        }
        // Prepare fields with the right syntax to be sent to server.
        String date = date_textView.getText().toString();
        String startingTime = date
                .concat( "T" )
                .concat( DateUtility.getUTCHHmmFromLocalHHmm( startingTime_textView.getText().toString() ) )
                .concat( ":00.000Z" );
        String endingTime = date
                .concat( "T" )
                .concat( DateUtility.getUTCHHmmFromLocalHHmm( endingTime_textView.getText().toString() ) )
                .concat( ":00.000Z" );

        // Start procedure to send request to server.
        waitForServerResponse();
        AddEventController addEventController = new AddEventController( addEventHandler );

        // Check what type of event is being created.
        if ( ( ( RadioButton ) findViewById( R.id.normalEvent_radioButton ) ).isChecked() ) {
            EventBody eventBody = new EventBody(
                    eventName_editText.getText().toString(),
                    description_editText.getText().toString(),
                    startingTime,
                    endingTime,
                    selectedEventLocation.getLocation(),
                    selectedDepartureLocation.getLocation(),
                    selectedPreference.getId(),
                    ( ( Switch ) findViewById( R.id.previousLocation_switch ) ).isChecked(),
                    startTravelingAtLast
            );
            addEventController.start( token, eventBody );
        } else {
            BreakEventBody breakEventBody = new BreakEventBody(
                    eventName_editText.getText().toString(),
                    startingTime,
                    endingTime,
                    DateUtility.getNoUTCSecondsFromHHmm( minimumTime_textView.getText().toString() )
            );
            addEventController.start( token, breakEventBody );
        }
    }

    /**
     * Checks the fields to assure the all the user's inputs are valid.
     * If not, user gets notified by a toast and errors are shown.
     */
    private boolean validate () {
        // Reset errors.
        eventName_editText.setError( null );
        date_textView.setError( null );
        startingTime_textView.setError( null );
        endingTime_textView.setError( null );

        boolean valid = true;
        View focusView = null;

        // Check for a non empty name.
        if ( eventName_editText.getText().length() == 0 ) {
            eventName_editText.setError( getString( R.string.error_field_required ) );
            focusView = eventName_editText;
            valid = false;
        }

        // Check for a non empty date.
        if ( date_textView.getText().length() == 0 ) {
            date_textView.setError( getString( R.string.error_field_required ) );
            focusView = date_textView;
            valid = false;
        }

        // Check for a non empty starting time.
        if ( startingTime_textView.getText().length() == 0 ) {
            startingTime_textView.setError( getString( R.string.error_field_required ) );
            focusView = startingTime_textView;
            valid = false;
        }

        // Check for a non empty ending time.
        if ( endingTime_textView.getText().length() == 0 ) {
            endingTime_textView.setError( getString( R.string.error_field_required ) );
            focusView = endingTime_textView;
            valid = false;
        }
        // Check which radio button is selected.
        if ( ( ( RadioButton ) findViewById( R.id.breakEvent_radioButton ) ).isChecked() ) {
            // Check for a non empty minimum time.
            if ( minimumTime_textView.getText().length() == 0 ) {
                minimumTime_textView.setError( getString( R.string.error_field_required ) );
                focusView = minimumTime_textView;
                valid = false;
            }
        }
        if ( !valid ) {
            // There was an error; focus the first form field with an error.
            focusView.requestFocus();
        }
        return valid;
    }

    /**
     * Sends request to server to receive locations of the user.
     * Locations received are saved in locationMap.
     */
    private void loadLocationsFromServer () {
        // Send request to server.
        waitForServerResponse();
        GetLocationsController getLocationsController = new GetLocationsController( getEventLocationsHandler );
        getLocationsController.start( token );
    }

    /**
     * Sends request to server to receive preferences of the user.
     * Preferences received are saved in preferenceMap.
     */
    private void loadPreferencesFromServer () {
        // Send request to server.
        waitForServerResponse();
        GetPreferencesController getPreferencesController = new GetPreferencesController( getEventPreferencesHandler );
        getPreferencesController.start( token );
    }

    /**
     * Populates the locations spinners with locations names from the locationMap.
     */
    public void populateLocationsSpinner () {
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter < String > adapter = new ArrayAdapter <>(
                getApplicationContext(),
                R.layout.black_spinner_item,
                locationMap.keySet().toArray( new String[ locationMap.size() ] )
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        // Apply the adapter to the spinners.
        eventLocation_spinner.setAdapter( adapter );
        departureLocation_spinner.setAdapter( adapter );
    }

    /**
     * Populates the typeOfEvent spinner with preferences names contained in preferenceMap.
     */
    public void populatePreferencesSpinner () {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter < String > adapter = new ArrayAdapter <>(
                getApplicationContext(),
                R.layout.black_spinner_item,
                preferenceMap.keySet().toArray( new String[ preferenceMap.size() ] )
        );
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        // Apply the adapter to the spinner
        typeOfEvent_spinner.setAdapter( adapter );
    }

    /**
     * Populates the startTravelingAt spinner with the choices "Earliest" and "Latest"
     */
    public void populateStartTravelingAtSpinner () {
        String[] choices = new String[]{ "Latest", "Earliest" };
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter < String > adapter = new ArrayAdapter <>(
                getApplicationContext(),
                R.layout.black_spinner_item,
                choices
        );
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        // Apply the adapter to the spinner
        startTravelingAt_spinner.setAdapter( adapter );
    }

    /**
     * Shows the right fields depending on the type of event currently selected.
     */
    public void onRadioButtonClicked ( View view ) {
        // Is the button now checked?
        boolean checked = ( ( RadioButton ) view ).isChecked();

        // Check which radio button was clicked
        switch ( view.getId() ) {
            case R.id.normalEvent_radioButton:
                if ( checked ) {
                    normalEvent_linearLayout.setVisibility( View.VISIBLE );
                    breakEvent_linearLayout.setVisibility( View.GONE );
                }
                break;
            case R.id.breakEvent_radioButton:
                if ( checked ) {
                    normalEvent_linearLayout.setVisibility( View.GONE );
                    breakEvent_linearLayout.setVisibility( View.VISIBLE );
                }
                break;
        }
    }

    /**
     * Shows the departure location field if the possibility is selected.
     */
    public void onSwitchClicked ( View view ) {
        boolean checked = ( ( Switch ) view ).isChecked();
        if ( !checked ) {
            findViewById( R.id.previousLocation_linearLayout ).setVisibility( View.VISIBLE );
        } else {
            findViewById( R.id.previousLocation_linearLayout ).setVisibility( View.GONE );
        }
    }

    /**
     * Allows the user to select a date.
     * Displays the selected date in the textView adjacent to the button.
     */
    public void showDatePickerDialog ( View view ) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTextView( findViewById( R.id.date_textView ) );
        newFragment.show( getFragmentManager(), "datePicker" );
    }

    /**
     * Allows the user to select a time.
     * Displays the selected time in the textView adjacent to the button.
     */
    public void showTimePickerDialog ( View view ) {
        TimePickerFragment newFragment = new TimePickerFragment();
        if ( view == findViewById( R.id.startingTime_button ) ) {
            newFragment.setTextView( findViewById( R.id.startingTime_textView ) );
        } else if ( view == findViewById( R.id.endingTime_button ) ) {
            newFragment.setTextView( findViewById( R.id.endingTime_textView ) );
        } else if ( view == findViewById( R.id.minimumTime_button ) ) {
            newFragment.setTextView( findViewById( R.id.minimumTime_textView ) );
        }
        newFragment.show( getFragmentManager(), "timePicker" );
    }

    @Override
    public void updateLocations ( Map < String, Location > locationMap ) {
        // If there are no locations saved, redirect to the activity account.
        if ( locationMap.isEmpty() ) {
            // Double toast for double time showed.
            Toast.makeText(
                    getBaseContext(),
                    "You need to add locations before you try to add an event!",
                    Toast.LENGTH_LONG
            ).show();
            Toast.makeText(
                    getBaseContext(),
                    "You need to add locations before you try to add an event!",
                    Toast.LENGTH_LONG
            ).show();
            startActivity( new Intent( this, AccountActivity.class ) );
        }
        this.locationMap = locationMap;
        populateLocationsSpinner();
        resumeNormalMode();
    }

    @Override
    public void updatePreferences ( Map < String, Preference > preferenceMap ) {
        this.preferenceMap = preferenceMap;
        populatePreferencesSpinner();
        resumeNormalMode();
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
