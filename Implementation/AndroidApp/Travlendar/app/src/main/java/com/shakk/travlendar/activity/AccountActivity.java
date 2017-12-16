package com.shakk.travlendar.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shakk.travlendar.Location;
import com.shakk.travlendar.R;
import com.shakk.travlendar.TravlendarRestClient;
import com.shakk.travlendar.database.view_model.UserViewModel;
import com.shakk.travlendar.retrofit.controller.GetLocationsController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

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
    private ProgressBar progressBar;

    private final int PLACE_PICKER_REQUEST = 1;
    private String univocalCode;

    private UserViewModel userViewModel;

    // Contain values taken from user input fields.
    private String locationName;
    private String locationAddress;
    private String latitude;
    private String longitude;

    // Handler for server responses.
    private Handler getterHandler;
    private Handler senderHandler;

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
        progressBar = findViewById(R.id.progressBar);

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
            univocalCode = user != null ? user.getUnivocalCode() : "";
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
                // Nothing happens.
            }
        });

        // Setup button listeners.
        deleteLocation_imageView.setOnClickListener(view -> deleteLocationFromServer());
        selectLocation_button.setOnClickListener(view -> selectLocationAddress());
        addLocation_button.setOnClickListener(view -> sendLocationToServer());

        // Handle server responses.
        getterHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 200:
                        Toast.makeText(getBaseContext(), "Locations updated!", Toast.LENGTH_LONG).show();

                        // Retrieve data from bundle.
                        Bundle bundle = msg.getData();
                        String jsonLocations = bundle.getString("jsonLocations");
                        List<Location> locations = new Gson().fromJson(jsonLocations, new TypeToken<List<Location>>(){}.getType());
                        locationsMap = new HashMap<>();
                        for (Location location : locations) {
                            locationsMap.put(location.getName(), location);
                        }
                        populateLocationsSpinner();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "Unknown error.", Toast.LENGTH_LONG).show();
                        Log.d("ERROR_RESPONSE", msg.toString());
                        break;
                }
                resumeNormalMode();
            }
        };

        // Handle server responses.
        senderHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 200:
                        Toast.makeText(getBaseContext(), "Locations updated!", Toast.LENGTH_LONG).show();

                        // Retrieve data from bundle.
                        Bundle bundle = msg.getData();
                        String jsonLocations = bundle.getString("jsonLocations");
                        List<Location> locations = new Gson().fromJson(jsonLocations, new TypeToken<List<Location>>(){}.getType());
                        locationsMap = new HashMap<>();
                        for (Location location : locations) {
                            locationsMap.put(location.getName(), location);
                        }
                        populateLocationsSpinner();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "Unknown error.", Toast.LENGTH_LONG).show();
                        Log.d("ERROR_RESPONSE", msg.toString());
                        break;
                }
                resumeNormalMode();
            }
        };
    }

    private void loadLocationsFromServer() {
        // Send request to server.
        waitForServerResponse();
        GetLocationsController getLocationsController = new GetLocationsController(getterHandler);
        getLocationsController.start(univocalCode);
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
     * Sends the location entered by the user to the server.
     */
    private void sendLocationToServer() {
        /* Store values at the time of the sending attempt.
        locationName = locationName_editText.getText().toString();

        if (!validate()) {
            Toast.makeText(getBaseContext(), "Something is wrong", Toast.LENGTH_LONG).show();
            return;
        }

        // Build JSON to be sent to server.
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("name", locationName);
            jsonParams.put("address", locationAddress);
            jsonParams.put("latitude", latitude);
            jsonParams.put("longitude", longitude);
            entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Send JSON to server.
        TravlendarRestClient.postWithAuth("ApplicationServerArchive/preference/location", univocalCode
                , entity, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                //Makes UI unresponsive.
                waitForServerResponse();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Notify the user that the location has been added.
                Toast.makeText(getBaseContext(), "Location added!", Toast.LENGTH_LONG).show();
                // Add location to the list.
                Location location = new Location(locationName, new Position(locationAddress));
                locationsMap.put(locationName, location);
                populateLocationsSpinner();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //Sending failed.
                switch (statusCode) {
                    case 400:
                        Toast.makeText(getBaseContext(), "Invalid fields sent to server!", Toast.LENGTH_LONG).show();
                        Log.d("ERROR_RESPONSE", responseString);
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "Unknown error.", Toast.LENGTH_LONG).show();
                        Log.d("ERROR_RESPONSE", responseString);
                        break;
                }
            }

            @Override
            public void onFinish() {
                //Makes UI responsive again.
                resumeNormalMode();
            }
        });*/
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

    private void deleteLocationFromServer() {
        // Retrieve location name to be deleted.
        String selectedLocation = locations_spinner.getSelectedItem().toString();

        //Send request to server.
        TravlendarRestClient.deleteWithAuth("ApplicationServerArchive/preference/location/".concat(selectedLocation), univocalCode
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        //Makes UI unresponsive.
                        waitForServerResponse();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // Notify the user that the location has been removed.
                        Toast.makeText(getBaseContext(), "Location removed!", Toast.LENGTH_LONG).show();
                        // Remove location to the list.
                        locationsMap.remove(locationName);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        // Request failed.
                        switch (statusCode) {
                            case 400:
                                Toast.makeText(getBaseContext(), "The location specified does not exist!", Toast.LENGTH_LONG).show();
                                Log.d("ERROR_RESPONSE", responseString);
                                break;
                            default:
                                Toast.makeText(getBaseContext(), "Unknown error.", Toast.LENGTH_LONG).show();
                                Log.d("ERROR_RESPONSE", responseString);
                                break;
                        }
                    }

                    @Override
                    public void onFinish() {
                        //Makes UI responsive again.
                        resumeNormalMode();
                    }
                });
    }

    private void populateLocationsSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext()
                , android.R.layout.simple_spinner_item
                , locationsMap.keySet().toArray(new String[locationsMap.size()]));
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        locations_spinner.setAdapter(adapter);
    }

    /**
     * Disables user input fields.
     */
    private void waitForServerResponse() {
        addLocation_button.setEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Enables user input fields.
     */
    private void resumeNormalMode() {
        addLocation_button.setEnabled(true);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);
    }
}
