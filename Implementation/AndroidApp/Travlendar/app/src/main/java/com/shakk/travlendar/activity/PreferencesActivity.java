package com.shakk.travlendar.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shakk.travlendar.Preference;
import com.shakk.travlendar.R;
import com.shakk.travlendar.TravlendarRestClient;
import com.shakk.travlendar.activity.fragment.TimePickerFragment;
import com.shakk.travlendar.database.view_model.UserViewModel;
import com.shakk.travlendar.retrofit.controller.DeletePreferenceController;
import com.shakk.travlendar.retrofit.controller.GetPreferencesController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class PreferencesActivity extends MenuActivity {

    //UI references.
    private ImageView deletePreference_imageView;
    private LinearLayout checkBoxes_linearLayout;
    private Spinner preferences_spinner;
    private Spinner preferredPath_spinner;
    private Spinner travelMeanConstrained_spinner;
    private TextView minTime_textView;
    private TextView maxTime_textView;
    private ProgressBar progressBar;

    private String univocalCode;
    private Map<String, Preference> preferencesMap = new HashMap<>();
    private Preference selectedPreference = new Preference();
    private String selectedTravelMeanConstrained;

    // Handlers for server responses.
    private Handler getterHandler;
    private Handler adderHandler;
    private Handler deleterHandler;

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

        // Setup preferencesMap spinner listeners.
        preferences_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Change the selected preference.
                selectedPreference = preferencesMap.get(adapterView.getSelectedItem().toString());
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
                selectedTravelMeanConstrained = Preference.TravelMeanEnum.values()[i].getTravelMean();
                setTravelMeanConstraints(selectedPreference, selectedTravelMeanConstrained);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nothing happens.
            }
        });

        // Setup button listeners.
        deletePreference_imageView.setOnClickListener(view -> deletePreferenceFromServer());

        // Handle server responses.
        getterHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Toast.makeText(getBaseContext(), "No internet connection available!", Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        Toast.makeText(getBaseContext(), "Preferences updated!", Toast.LENGTH_LONG).show();

                        // Retrieve data from bundle.
                        Bundle bundle = msg.getData();
                        String jsonPreferences = bundle.getString("jsonPreferences");
                        List<Preference> preferences = new Gson()
                                .fromJson(
                                        jsonPreferences,
                                        new TypeToken<List<Preference>>(){}.getType()
                                );
                        preferencesMap = new HashMap<>();
                        for (Preference preference : preferences) {
                            preferencesMap.put(preference.getName(), preference);
                        }
                        populatePreferencesSpinner();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "Unknown error.", Toast.LENGTH_LONG).show();
                        Log.d("ERROR_RESPONSE", msg.toString());
                        break;
                }
                resumeNormalMode();
            }
        };

        // TODO: Handle server responses.
        adderHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Toast.makeText(getBaseContext(), "No internet connection available!", Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        Toast.makeText(getBaseContext(), "Preferences updated!", Toast.LENGTH_LONG).show();

                        // Retrieve data from bundle.
                        Bundle bundle = msg.getData();
                        String jsonPreferences = bundle.getString("jsonPreferences");
                        List<Preference> preferences = new Gson()
                                .fromJson(
                                        jsonPreferences,
                                        new TypeToken<List<Preference>>(){}.getType()
                                );
                        preferencesMap = new HashMap<>();
                        for (Preference preference : preferences) {
                            preferencesMap.put(preference.getName(), preference);
                        }
                        populatePreferencesSpinner();
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
        deleterHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Toast.makeText(getBaseContext(), "No internet connection available!", Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        // Notify the user that the preference has been removed.
                        Toast.makeText(getBaseContext(), "Preference removed!", Toast.LENGTH_LONG).show();
                        // Remove preference from the list.
                        preferencesMap.remove(selectedPreference.getName());
                        break;
                    case 400:
                        Toast.makeText(getBaseContext(), "The specified profile does not exist", Toast.LENGTH_LONG).show();
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

    private void loadPreferencesFromServer() {
        // Send request to server.
        waitForServerResponse();
        GetPreferencesController getPreferencesController = new GetPreferencesController(getterHandler);
        getPreferencesController.start(univocalCode);
    }

    private void populatePreferencesSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                preferencesMap.keySet().toArray(new String[preferencesMap.size()])
        );
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
        for (Preference.TravelMeanEnum travelMean : Preference.TravelMeanEnum.values()) {
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
        Preference.ParamFirstPath param = preference.getParamFirstPath();
        for (int i = 0; i < Preference.ParamFirstPath.values().length; i++) {
            if (Preference.ParamFirstPath.values()[i] == param) {
                index = i;
            }
        }
        preferredPath_spinner.setSelection(index);
    }

    private void setTravelMeanConstraints(Preference preference, String travelMean) {
        List<Preference.Constraint> constraints = preference.getLimitedBy(travelMean);
        minTime_textView.setText("00:00");
        maxTime_textView.setText("24:00");
        for (Preference.Constraint constraint : constraints) {
            if (! (constraint.getMinHour() == 0 && constraint.getMaxHour() == 0)) {
                minTime_textView.setText(Integer.toString(constraint.getMinHour()));
                maxTime_textView.setText(Integer.toString(constraint.getMaxHour()));
            }
        }
    }

    private void deletePreferenceFromServer() {
        // Send request to server.
        waitForServerResponse();
        DeletePreferenceController deletePreferenceController = new DeletePreferenceController(deleterHandler);
        deletePreferenceController.start(univocalCode, selectedPreference.getId());
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
}