package it.polimi.travlendarplus.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.TravelMeanEnum;
import it.polimi.travlendarplus.activity.fragment.DatePickerFragment;
import it.polimi.travlendarplus.activity.handler.LocationLoader;
import it.polimi.travlendarplus.activity.handler.location.GetLocationsHandler;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.location.GetLocationsController;

/**
 * Activity that allows the creation and editing of a ticket.
 * To be implemented.
 */
public class TicketEditorActivity extends MenuActivity implements LocationLoader {
    // UI references.
    private LinearLayout checkBoxes_linearLayout;
    private Spinner startingLocation_spinner;
    private Spinner endingLocation_spinner;
    private CheckBox period_checkBox;
    // View models.
    private UserViewModel userViewModel;
    // Variables.
    private String token;
    private boolean firstLaunch = true;
    private Map<String, Location> locationMap;
    // Handlers.
    private Handler getLocationsHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_editor);
        super.setupMenuToolbar();
        // UI references setup.
        checkBoxes_linearLayout = findViewById(R.id.checkBoxes_linearLayout);
        startingLocation_spinner = findViewById(R.id.startingLocation_spinner);
        endingLocation_spinner = findViewById(R.id.endingLocation_spinner);
        period_checkBox = findViewById(R.id.period_checkBox);
        // View models setup.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, user -> {
            token = user != null ? user.getToken() : "";
            // On the first launch, download locations from the server.
            if (firstLaunch) {
                createCheckBoxes();
                loadLocationsFromServer();
                firstLaunch = false;
            }
        });
        //Handlers setup.
        getLocationsHandler = new GetLocationsHandler(Looper.getMainLooper(), getApplicationContext(), this);
    }

    /**
     * Sets the right checkboxes for travel means of the selected preference
     * in the checkBoxes_linearLayout.
     * Every checkbox has a listener that updates the selected preference constraints.
     */
    private void createCheckBoxes() {
        // Create list of public travel means.
        List<TravelMeanEnum> publicTravelMeans = new ArrayList<>();
        publicTravelMeans.add(TravelMeanEnum.BUS);
        publicTravelMeans.add(TravelMeanEnum.SUBWAY);
        publicTravelMeans.add(TravelMeanEnum.TRAIN);
        publicTravelMeans.add(TravelMeanEnum.TRAM);
        // Create a checkbox for each public travel mean.
        for (TravelMeanEnum travelMean : publicTravelMeans) {
            // Setup checkbox.
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(travelMean.getTravelMean());
            checkBox.setLayoutParams(new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // Add checkbox to linear layout.
            checkBoxes_linearLayout.addView(checkBox);
        }
        // Add listener to period checkbox.
        period_checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (! checked) {
                findViewById(R.id.period_linearLayout).setVisibility(View.GONE);
            } else {
                findViewById(R.id.period_linearLayout).setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Sends request to the server to get locations available.
     */
    private void loadLocationsFromServer() {
        // Send request to server.
        waitForServerResponse();
        GetLocationsController getLocationsController = new GetLocationsController(getLocationsHandler);
        getLocationsController.start(token);
    }

    /**
     * Populate the location spinners with locations previously got from the server.
     * Locations are stored in locationMap.
     */
    public void populateLocationSpinners() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                locationMap.keySet().toArray(new String[locationMap.size()])
        );
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        startingLocation_spinner.setAdapter(adapter);
        endingLocation_spinner.setAdapter(adapter);
    }

    /**
     * When a radio button is clicked, depending on the ticket type
     * selection a different layout get shown and the other ones gets hidden.
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.genericTicket_radioButton:
                if (checked) {
                    findViewById(R.id.genericTicket_linearLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.distanceTicket_linearLayout).setVisibility(View.GONE);
                }
                break;
            case R.id.distanceTicket_radioButton:
                if (checked) {
                    findViewById(R.id.genericTicket_linearLayout).setVisibility(View.GONE);
                    findViewById(R.id.distanceTicket_linearLayout).setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Allows the user to select a date.
     * Displays the selected date in the textView adjacent to the button.
     */
    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        if (view == findViewById(R.id.startDate_button)) {
            newFragment.setTextView(findViewById(R.id.startDate_textView));
        } else if (view == findViewById(R.id.endDate_button)) {
            newFragment.setTextView(findViewById(R.id.endDate_textView));
        }
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void updateLocations(Map<String, Location> locationMap) {
        this.locationMap = locationMap;
        populateLocationSpinners();
        resumeNormalMode();
    }
}
