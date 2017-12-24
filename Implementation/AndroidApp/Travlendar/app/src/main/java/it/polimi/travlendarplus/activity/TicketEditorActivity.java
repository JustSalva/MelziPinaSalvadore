package it.polimi.travlendarplus.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.Position;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.TravelMeanEnum;
import it.polimi.travlendarplus.TravelMeanUsed;
import it.polimi.travlendarplus.activity.fragment.DatePickerFragment;
import it.polimi.travlendarplus.activity.handler.LocationLoader;
import it.polimi.travlendarplus.activity.handler.location.GetLocationsHandler;
import it.polimi.travlendarplus.activity.handler.ticket.AddTicketHandler;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.body.ticket.DistanceTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.GenericTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.PathTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.PeriodTicketBody;
import it.polimi.travlendarplus.retrofit.body.ticket.TicketBody;
import it.polimi.travlendarplus.retrofit.controller.location.GetLocationsController;
import it.polimi.travlendarplus.retrofit.controller.ticket.AddTicketController;

/**
 * Activity that allows the creation and editing of a ticket.
 * To be implemented.
 */
public class TicketEditorActivity extends MenuActivity implements LocationLoader {
    // UI references.
    private LinearLayout checkBoxes_linearLayout;
    private RadioButton genericTicket_radioButton;
    private RadioButton distanceTicket_radioButton;
    private AutoCompleteTextView cost_editText;
    private AutoCompleteTextView lineName_editText;
    private AutoCompleteTextView distance_editText;
    private Spinner startingLocation_spinner;
    private Spinner endingLocation_spinner;
    private CheckBox path_checkBox;
    private CheckBox period_checkBox;
    private TextView startDate_textView;
    private TextView endDate_textView;
    // View models.
    private UserViewModel userViewModel;
    // Variables.
    private String token;
    private boolean firstLaunch = true;
    private Map<String, Location> locationMap;
    private Location startingLocation;
    private Location endingLocation;
    private List<TravelMeanUsed> relatedTo;
    // Handlers.
    private Handler getLocationsHandler;
    private Handler addTicketHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_editor);
        super.setupMenuToolbar();
        // UI references setup.
        checkBoxes_linearLayout = findViewById(R.id.checkBoxes_linearLayout);
        genericTicket_radioButton = findViewById(R.id.genericTicket_radioButton);
        distanceTicket_radioButton = findViewById(R.id.distanceTicket_radioButton);
        cost_editText = findViewById(R.id.cost_editText);
        lineName_editText = findViewById(R.id.lineName_editText);
        distance_editText = findViewById(R.id.distance_editText);
        startingLocation_spinner = findViewById(R.id.startingLocation_spinner);
        endingLocation_spinner = findViewById(R.id.endingLocation_spinner);
        path_checkBox = findViewById(R.id.pathTicket_checkBox);
        period_checkBox = findViewById(R.id.period_checkBox);
        startDate_textView = findViewById(R.id.startDate_textView);
        endDate_textView = findViewById(R.id.endDate_textView);
        // View models setup.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, user -> {
            token = user != null ? user.getToken() : "";
            // On the first launch, download locations from the server.
            if (firstLaunch) {
                createCheckBoxes();
                loadLocationsFromServer();
                relatedTo = new ArrayList<>();
                firstLaunch = false;
            }
        });
        //Handlers setup.
        getLocationsHandler = new GetLocationsHandler(Looper.getMainLooper(), getApplicationContext(), this);
        addTicketHandler = new AddTicketHandler(Looper.getMainLooper(), getApplicationContext(), this);
        // Spinners listeners.
        // Setup travel mean constrained spinner listener.
        startingLocation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                startingLocation = locationMap.get(adapterView.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nothing happens.
            }
        });
        endingLocation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                endingLocation = locationMap.get(adapterView.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nothing happens.
            }
        });
        // Setup button listener.
        findViewById(R.id.confirm_button).setOnClickListener(click -> addTicket());
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
            // Add listener.
            checkBox.setOnCheckedChangeListener(((compoundButton, checked) -> {
                if (checked) {
                    relatedTo.add(
                            new TravelMeanUsed(TravelMeanEnum.getEnumFromString(travelMean.getTravelMean()), travelMean.getTravelMean())
                    );
                } else {
                    relatedTo.remove(
                            new TravelMeanUsed(TravelMeanEnum.getEnumFromString(travelMean.getTravelMean()), travelMean.getTravelMean())
                    );
                }
            }));
            // Add checkbox to linear layout.
            checkBoxes_linearLayout.addView(checkBox);
        }
        // Add listener to path checkBox.
        path_checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                findViewById(R.id.path_linearLayout).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.path_linearLayout).setVisibility(View.GONE);
            }
        });
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
     * Collects data from input fields and sends add ticket request to server.
     */
    private void addTicket() {
        if (!validate()) {
            Toast.makeText(getBaseContext(), "Something is wrong", Toast.LENGTH_LONG).show();
            return;
        }
        float cost = Float.parseFloat(cost_editText.getText().toString());

        // Is it a generic ticket?
        GenericTicketBody genericTicketBody = null;
        PathTicketBody pathTicketBody = null;
        DistanceTicketBody distanceTicketBody = null;
        if (genericTicket_radioButton.isChecked()) {
            genericTicketBody = new GenericTicketBody(
                    cost,
                    relatedTo,
                    lineName_editText.getText().toString()
            );
            if (path_checkBox.isChecked()) {
                pathTicketBody = new PathTicketBody(
                        cost,
                        relatedTo,
                        lineName_editText.getText().toString(),
                        startingLocation.getLocation(),
                        endingLocation.getLocation()
                );
            }
        } else if (distanceTicket_radioButton.isChecked()) { // or is it distance?
            distanceTicketBody = new DistanceTicketBody(
                    cost,
                    relatedTo,
                    Integer.parseInt(distance_editText.getText().toString())
            );
        }
        // Is it a period ticket?
        PeriodTicketBody periodTicketBody = null;
        if (period_checkBox.isChecked()) {
            periodTicketBody = new PeriodTicketBody(
                    cost,
                    relatedTo,
                    lineName_editText.getText().toString(),
                    startDate_textView.getText().toString().concat("T00:00:00.000Z"),
                    endDate_textView.getText().toString().concat("T00:00:00.000Z")
            );
            // Apply decorators.
            periodTicketBody.setGenericDecorator(genericTicketBody);
            periodTicketBody.setPathDecorator(pathTicketBody);
            periodTicketBody.setDistanceDecorator(distanceTicketBody);
        }

        // Send request to server.
        waitForServerResponse();
        AddTicketController addTicketController = new AddTicketController(addTicketHandler);
        if (periodTicketBody != null) {
            addTicketController.addPeriodTicket(token, periodTicketBody);
        } else if (pathTicketBody != null) {
            addTicketController.addPathTicket(token, pathTicketBody);
        } else if (genericTicketBody != null) {
            addTicketController.addGenericTicket(token, genericTicketBody);
        } else if (distanceTicketBody != null) {
            addTicketController.addDistanceTicket(token, distanceTicketBody);
        }
    }

    /**
     * Checks if the user inputs are correct.
     * @return true if correct, false otherwise.
     */
    private boolean validate() {
        TextView travelMeans_textView = findViewById(R.id.travelMeans_textView);
        // Reset errors.
        travelMeans_textView.setError(null);
        cost_editText.setError(null);

        boolean valid = true;
        View focusView = null;
        // Check if at least one checkBox is selected.
        if (relatedTo.isEmpty()) {
            travelMeans_textView.setError(getString(R.string.error_field_required));
            focusView = travelMeans_textView;
            valid = false;
        }
        // Check for a valid cost.
        if (TextUtils.isEmpty(cost_editText.getText())) {
            cost_editText.setError(getString(R.string.error_field_required));
            focusView = cost_editText;
            valid = false;
        }
        // Check for a valid generic ticket.
        if (genericTicket_radioButton.isChecked()) {
            // Check if line name is inserted.
            if (TextUtils.isEmpty(lineName_editText.getText())) {
                lineName_editText.setError(getString(R.string.error_field_required));
                focusView = lineName_editText;
                valid = false;
            }
        }
        // Check for a valid distance ticket.
        if (distanceTicket_radioButton.isChecked()) {
            // Check if distance is inserted.
            if (TextUtils.isEmpty(distance_editText.getText())) {
                distance_editText.setError(getString(R.string.error_field_required));
                focusView = distance_editText;
                valid = false;
            }
        }
        // Check for a valid period ticket.
        if (period_checkBox.isChecked()) {
            if (TextUtils.isEmpty(startDate_textView.getText())) {
                startDate_textView.setError(getString(R.string.error_field_required));
                focusView = startDate_textView;
                valid = false;
            }
            if (TextUtils.isEmpty(endDate_textView.getText())) {
                endDate_textView.setError(getString(R.string.error_field_required));
                focusView = endDate_textView;
                valid = false;
            }
        }
        if (!valid) {
            // There was an error; focus the first form field with an error.
            focusView.requestFocus();
        }

        return valid;
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
