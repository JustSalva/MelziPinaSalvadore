package it.polimi.travlendarplus.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.handler.location.AddLocationHandler;
import it.polimi.travlendarplus.activity.handler.location.DeleteLocationHandler;
import it.polimi.travlendarplus.activity.handler.location.GetAccountLocationsHandler;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.AddLocationController;
import it.polimi.travlendarplus.retrofit.controller.DeleteLocationController;
import it.polimi.travlendarplus.retrofit.controller.GetLocationsController;

import java.util.Map;

/**
 * Activity the lets the user see his personal information and location.
 * It allows the user to add and delete locations.
 */
public class AccountActivity extends MenuActivity {
    // UI references.
    private TextView name_textView;
    private TextView surname_textView;
    private TextView email_textView;
    private Spinner locations_spinner;
    private TextView locationsAddressViewer_textView;
    private ImageView deleteLocation_imageView;
    private TextView locationAddress_textView;
    private EditText locationName_editText;
    private Button selectLocation_button;
    private Button addLocation_button;

    private final int PLACE_PICKER_REQUEST = 1;
    private String token;
    private UserViewModel userViewModel;

    // Contain values taken from user input fields.
    private String locationName;
    private String locationAddress;
    private String latitude;
    private String longitude;

    // Handler for server responses.
    private Handler getLocationsHandler;
    private Handler addLocationHandler;
    private Handler deleteLocationHandler;

    // Store locations received by the server.
    private Map<String, Location> locationsMap;
    private Location selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        super.setupMenuToolbar();

        //Find layout views.
        name_textView = findViewById(R.id.name);
        surname_textView = findViewById(R.id.surname);
        email_textView = findViewById(R.id.email);
        locations_spinner = findViewById(R.id.locations_spinner);
        locationsAddressViewer_textView = findViewById(R.id.locationsAddressViewer_textView);
        deleteLocation_imageView = findViewById(R.id.deleteLocation_imageView);
        locationName_editText = findViewById(R.id.locationName_editText);
        locationAddress_textView = findViewById(R.id.locationAddress_textView);
        selectLocation_button = findViewById(R.id.selectLocation_button);
        addLocation_button = findViewById(R.id.addLocation_button);

        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activity receive the same MyViewModel instance created by the first activity.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, user -> {
            String name = user != null ? user.getName() : "";
            name_textView.setText(name);
            String surname = user != null ? user.getSurname() : "";
            surname_textView.setText(surname);
            String email = user != null ? user.getEmail() : "";
            email_textView.setText(email);
            token = user != null ? user.getToken() : "";
            // To be called only on the first onCreate().
            if (savedInstanceState == null) {
                loadLocationsFromServer();
            }
        });

        //Setup locations spinner listeners.
        locations_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Shows address of the selected locations next to the spinner.
                selectedLocation = locationsMap.get(adapterView.getSelectedItem().toString());
                locationsAddressViewer_textView.setText(selectedLocation.getLocation().getAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                locationsAddressViewer_textView.setText("");
            }
        });

        // Setup button listeners.
        deleteLocation_imageView.setOnClickListener(view -> deleteLocationFromServer());
        selectLocation_button.setOnClickListener(view -> selectLocationAddress());
        addLocation_button.setOnClickListener(view -> sendLocationToServer());

        // Handle server responses.
        getLocationsHandler = new GetAccountLocationsHandler(Looper.getMainLooper(), getApplicationContext(), this);
        addLocationHandler = new AddLocationHandler(Looper.getMainLooper(), getApplicationContext(), this);
        deleteLocationHandler = new DeleteLocationHandler(Looper.getMainLooper(), getApplicationContext(), this);
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
     * Starts an intent that allows the user to pick a place location using Google APIs.
     * After selection onActivityResult(...) is called.
     */
    private void selectLocationAddress() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets called after the selection of a place by selectLocationAddress().
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                locationAddress = place.getAddress().toString();
                locationAddress_textView.setText(locationAddress);
                latitude = Double.toString(place.getLatLng().latitude);
                longitude = Double.toString(place.getLatLng().longitude);
            }
        }
    }

    /**
     * Checks the values inserted from the user. If correct, sends the location specified to the server.
     */
    private void sendLocationToServer() {
        // Store values at the time of the sending attempt.
        locationName = locationName_editText.getText().toString();
        // check values entered.
        if (!validate()) {
            Toast.makeText(getBaseContext(), "Something is wrong", Toast.LENGTH_LONG).show();
            return;
        }
        // Send request to server.
        waitForServerResponse();
        AddLocationController addLocationController = new AddLocationController(addLocationHandler);
        addLocationController.start(
                token,
                locationName,
                locationAddress,
                latitude,
                longitude
        );
    }

    /**
     * Checks if the user inputs are correct.
     * @return true if correct, false otherwise.
     */
    private boolean validate() {
        // Reset errors.
        locationName_editText.setError(null);

        boolean valid = true;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(locationName)) {
            locationName_editText.setError(getString(R.string.error_field_required));
            focusView = locationName_editText;
            valid = false;
        }

        if (!valid) {
            // There was an error; focus the first form field with an error.
            focusView.requestFocus();
        }

        return valid;
    }

    /**
     * Sends request to the server asking to delete a location specified by its name.
     */
    private void deleteLocationFromServer() {
        // Send request to server.
        waitForServerResponse();
        DeleteLocationController deleteLocationController = new DeleteLocationController(deleteLocationHandler);
        deleteLocationController.start(token, selectedLocation.getName());
    }

    /**
     * Populate the locations spinner with locations previously got from the server.
     * Locations are stored in locationsMap.
     */
    public void populateLocationsSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                locationsMap.keySet().toArray(new String[locationsMap.size()])
        );
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        locations_spinner.setAdapter(adapter);
    }

    /**
     * Disables user input fields.
     */
    public void waitForServerResponse() {
        addLocation_button.setEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    /**
     * Enables user input fields.
     */
    public void resumeNormalMode() {
        addLocation_button.setEnabled(true);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    public Map<String, Location> getLocationsMap() {
        return locationsMap;
    }

    public void setLocationsMap(Map<String, Location> locationsMap) {
        this.locationsMap = locationsMap;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public Location getSelectedLocation() {
        return selectedLocation;
    }
}
