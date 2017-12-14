package com.shakk.travlendar.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shakk.travlendar.R;
import com.shakk.travlendar.TravlendarRestClient;
import com.shakk.travlendar.activity.fragment.TimePickerFragment;
import com.shakk.travlendar.database.view_model.UserViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class PreferencesActivity extends MenuActivity {

    private ImageView deletePreference_imageView;
    private LinearLayout checkBoxes_linearLayout;
    private Spinner preferences_spinner;
    private Spinner preferredPath_spinner;
    private Spinner travelMeanConstrained_spinner;
    private TextView minTime_textView;
    private TextView maxTime_textView;
    private ProgressBar progressBar;

    private String univocalCode;
    private Map<String, Preference> preferences = new HashMap<>();
    private Preference selectedPreference = new Preference();
    private String selectedTravelMeanConstrained;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        super.setupMenuToolbar();

        // UI references.
        deletePreference_imageView = findViewById(R.id.deletePreference_imageView);
        checkBoxes_linearLayout = findViewById(R.id.checkBoxes_linearLayout);
        preferences_spinner = findViewById(R.id.preferences_spinner);
        preferredPath_spinner = findViewById(R.id.preferredPath_spinner);
        travelMeanConstrained_spinner = findViewById(R.id.travelMeanConstrained_spinner);
        minTime_textView = findViewById(R.id.minTime_textView);
        maxTime_textView = findViewById(R.id.maxTime_textView);
        progressBar = findViewById(R.id.progressBar);

        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activity receive the same MyViewModel instance created by the first activity.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, user -> {
            univocalCode = user != null ? user.getUnivocalCode() : "";
            // To be called only on the first onCreate().
            if (savedInstanceState == null) {
                loadPreferencesFromServer();
            }
        });

        // Setup preferences spinner listeners.
        preferences_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Change the selected preference.
                selectedPreference = preferences.get(adapterView.getSelectedItem().toString());
                createTravelMeansCheckBoxes(selectedPreference);
                setPreferredPathSpinner(selectedPreference);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nothing happens.
            }
        });

        // Setup travel mean constrained spinner listeners.
        travelMeanConstrained_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Change the selected travel men constrained.
                selectedTravelMeanConstrained = TravelMeanEnum.values()[i].travelMean;
                setTravelMeanConstraints(selectedPreference, selectedTravelMeanConstrained);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nothing happens.
            }
        });

        // Setup button listeners.
        deletePreference_imageView.setOnClickListener(view -> deletePreferenceFromServer());
    }

    private void loadPreferencesFromServer() {
        //Send request to server.
        TravlendarRestClient.getWithAuth("ApplicationServerArchive/preference", univocalCode
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        //Makes UI unresponsive.
                        waitForServerResponse();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d("JSON_REPLY", response.toString());
                        Toast.makeText(getBaseContext(), "Preferences updated!", Toast.LENGTH_LONG).show();
                        //Get locations array from JSON response.
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                Gson gson = new Gson();
                                Preference preference = gson
                                        .fromJson(String.valueOf(response.getJSONObject(i)), Preference.class);
                                preferences.put(preference.name, preference);
                                populatePreferencesSpinner();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getBaseContext(), "Unknown error.", Toast.LENGTH_LONG).show();
                        Log.d("ERROR_RESPONSE", responseString);
                    }

                    @Override
                    public void onFinish() {
                        //Makes UI responsive again.
                        resumeNormalMode();
                    }
                });
    }

    private void populatePreferencesSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext()
                , android.R.layout.simple_spinner_item
                , preferences.keySet().toArray(new String[preferences.size()]));
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        preferences_spinner.setAdapter(adapter);
    }

    /**
     * Adds checkboxes for travel means to the checkBoxes_linearLayout.
     */
    private void createTravelMeansCheckBoxes(Preference preference) {
        // Clear old checkboxes.
        checkBoxes_linearLayout.removeAllViews();
        // Create a checkbox for each travel mean in the enum class.
        for (TravelMeanEnum travelMean : TravelMeanEnum.values()) {
            // Setup checkbox.
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(travelMean.getTravelMean());
            checkBox.setLayoutParams(new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // If the travel mean is activated, check box is selected.
            checkBox.setChecked(preference.isActivated(travelMean));
            // Add checkbox to linear layout.
            checkBoxes_linearLayout.addView(checkBox);
        }
    }

    private void setPreferredPathSpinner(Preference preference) {
        int index = 0;
        ParamFirstPath param = preference.paramFirstPath;
        for (int i = 0; i < ParamFirstPath.values().length; i++) {
            if (ParamFirstPath.values()[i] == param) {
                index = i;
            }
        }
        preferredPath_spinner.setSelection(index);
    }

    private void setTravelMeanConstraints(Preference preference, String travelMean) {
        List<Constraint> constraints = preference.getLimitedBy(travelMean);
        minTime_textView.setText("00:00");
        maxTime_textView.setText("24:00");
        for (Constraint constraint : constraints) {
            if (! (constraint.minHour == 0 && constraint.maxHour == 0)) {
                minTime_textView.setText(Integer.toString(constraint.minHour));
                maxTime_textView.setText(Integer.toString(constraint.maxHour));
            }
        }
    }

    private void deletePreferenceFromServer() {
        // Retrieve preference name to be deleted.
        String idPreference = Integer.toString(preferences.get(selectedPreference.name).id);

        //Send request to server.
        TravlendarRestClient.deleteWithAuth("ApplicationServerArchive/preference/".concat(idPreference), univocalCode
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        //Makes UI unresponsive.
                        waitForServerResponse();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // Notify the user that the preference has been removed.
                        Toast.makeText(getBaseContext(), "Preference removed!", Toast.LENGTH_LONG).show();
                        // Remove preference from the list.
                        preferences.remove(selectedPreference.name);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        // Request failed.
                        // TODO: error messages.
                        switch (statusCode) {
                            case 400:
                                Toast.makeText(getBaseContext(), "The preference specified does not exist!", Toast.LENGTH_LONG).show();
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

    /**
     * Disables user input fields.
     */
    private void waitForServerResponse() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Enables user input fields.
     */
    private void resumeNormalMode() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);
    }

    public void showTimePickerDialog(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        if (view == findViewById(R.id.minTime_button)) {
            newFragment.setTextView(findViewById(R.id.minTime_textView));
        } else if (view== findViewById(R.id.maxTime_button)) {
            newFragment.setTextView(findViewById(R.id.maxTime_textView));
        }
        newFragment.show(getFragmentManager(), "timePicker");
    }

    private class Preference {
        private int id;
        private String name;
        private ParamFirstPath paramFirstPath;
        private List<Constraint> limitedBy;
        private List<TravelMeanEnum> deactivate;

        Preference() {
            this.id = 0;
            this.name = "Standard";
            this.paramFirstPath = ParamFirstPath.MIN_TIME;
            this.limitedBy = new ArrayList<>();
            this.deactivate = new ArrayList<>();
        }

        List<Constraint> getLimitedBy(String travelMean) {
            List<Constraint> constraints = new ArrayList<>();
            for (Constraint constraint : limitedBy) {
                if (constraint.concerns.getTravelMean().equals(travelMean)) {
                    constraints.add(constraint);
                }
            }
            return constraints;
        }

        boolean isActivated(TravelMeanEnum travelMean) {
            return !deactivate.contains(travelMean);
        }
    }

    private class Constraint {
        private int id;
        private TravelMeanEnum concerns;
        private int minHour;
        private int maxHour;
        private int minLength;
        private int maxLength;
    }

    private enum ParamFirstPath {
        MIN_COST ("Minimum cost"),
        MIN_LENGTH ("Minimum length"),
        MIN_TIME ("Minimum time"),
        ECO_PATH ("Eco-friendly");

        private String text;

        ParamFirstPath(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private enum TravelMeanEnum {
        BIKE ("Bike"),
        BUS ("Bus"),
        BY_FOOT ("By Foot"),
        CAR ("Car"),
        SUBWAY ("Subway"),
        TRAIN ("Train"),
        TRAM ("Tram"),
        SHARING_BIKE ("Sharing Bike"),
        SHARING_CAR ("Sharing Car");

        private final String travelMean;

        TravelMeanEnum(String travelMean) {
            this.travelMean = travelMean;
        }

        public String getTravelMean() {
            return travelMean;
        }
    }
}