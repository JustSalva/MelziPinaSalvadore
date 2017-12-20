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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.fragment.TimePickerFragment;
import it.polimi.travlendarplus.activity.handler.AddPreferenceHandler;
import it.polimi.travlendarplus.activity.handler.DeletePreferenceHandler;
import it.polimi.travlendarplus.activity.handler.GetPreferencesHandler;
import it.polimi.travlendarplus.activity.handler.ModifyPreferenceHandler;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.body.PreferenceBody;
import it.polimi.travlendarplus.retrofit.controller.AddPreferenceController;
import it.polimi.travlendarplus.retrofit.controller.DeletePreferenceController;
import it.polimi.travlendarplus.retrofit.controller.GetPreferencesController;
import it.polimi.travlendarplus.retrofit.controller.ModifyPreferenceController;

import java.util.HashMap;
import java.util.Map;

public class PreferencesActivity extends MenuActivity {

    //UI references.
    private ImageView deletePreference_imageView;
    private LinearLayout checkBoxes_linearLayout;
    private Spinner preferences_spinner;
    private Spinner preferredPath_spinner;
    private Spinner travelMeanConstrained_spinner;
    private TextView minTime_textView;
    private TextView maxTime_textView;
    private AutoCompleteTextView minDistance_editText;
    private AutoCompleteTextView maxDistance_editText;

    private String token;
    private Map<String, Preference> preferencesMap;
    private Preference selectedPreference;
    private String selectedTravelMean;

    // Handlers for server responses.
    private Handler getPreferencesHandler;
    private Handler addPreferenceHandler;
    private Handler modifyPreferenceHandler;
    private Handler deletePreferenceHandler;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        super.setupMenuToolbar();

        // UI references.
        deletePreference_imageView = findViewById(R.id.deletePreference_imageView);
        checkBoxes_linearLayout = findViewById(R.id.checkBoxes_linearLayout);
        // Spinners.
        preferences_spinner = findViewById(R.id.preferences_spinner);
        preferredPath_spinner = findViewById(R.id.preferredPath_spinner);
        travelMeanConstrained_spinner = findViewById(R.id.travelMeanConstrained_spinner);
        // Period and distance constraints fields.
        minTime_textView = findViewById(R.id.minTime_textView);
        maxTime_textView = findViewById(R.id.maxTime_textView);
        minDistance_editText = findViewById(R.id.minDistance_editText);
        maxDistance_editText = findViewById(R.id.maxDistance_editText);

        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activity receive the same MyViewModel instance created by the first activity.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, user -> {
            token = user != null ? user.getToken() : "";
            // To be called only on the first onCreate().
            if (savedInstanceState == null) {
                selectedPreference = new Preference();
                loadPreferencesFromServer();
            }
        });

        // Setup preferences spinner listener.
        preferences_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Change the selected preference.
                selectedPreference = preferencesMap.get(adapterView.getSelectedItem().toString());

                createTravelMeansCheckBoxes();
                setPreferredPathSpinner();
                fillConstraints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nothing happens.
            }
        });

        // Setup preferred path spinner listener.
        preferredPath_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPreference.setParamFirstPath(
                        Preference.ParamFirstPath
                                .getEnumFromString(adapterView.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nothing happens.
            }
        });

        // Setup travel mean constrained spinner listener.
        travelMeanConstrained_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Save edited values.
                saveEditedConstraints();
                // Change the selected travel men constrained.
                selectedTravelMean = Preference.TravelMeanEnum.values()[i].getTravelMean();
                fillConstraints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nothing happens.
            }
        });

        // Setup button listeners.
        deletePreference_imageView.setOnClickListener(view -> deletePreferenceFromServer());
        findViewById(R.id.editPreference_button).setOnClickListener(view -> modifyPreferenceToServer());
        findViewById(R.id.addNewPreference_button).setOnClickListener(view -> addPreferenceToServer());

        // Handle server responses.
        getPreferencesHandler = new GetPreferencesHandler(Looper.getMainLooper(), getApplicationContext(), this);
        addPreferenceHandler = new AddPreferenceHandler(Looper.getMainLooper(), getApplicationContext(), this);
        modifyPreferenceHandler = new ModifyPreferenceHandler(Looper.getMainLooper(), getApplicationContext(), this);
        deletePreferenceHandler = new DeletePreferenceHandler(Looper.getMainLooper(), getApplicationContext(), this);
    }

    private void loadPreferencesFromServer() {
        // Initialize preferences map with standard type of event.
        preferencesMap = new HashMap<>();
        preferencesMap.put("Normal", new Preference());
        // Send request to server.
        waitForServerResponse();
        GetPreferencesController getPreferencesController = new GetPreferencesController(getPreferencesHandler);
        getPreferencesController.start(token);
    }

    public void populatePreferencesSpinner() {
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
     * Sets the right checkboxes for travel means of the selected preference
     * in the checkBoxes_linearLayout.
     */
    private void createTravelMeansCheckBoxes() {
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
            checkBox.setChecked(selectedPreference.isActivated(travelMean));
            // Setup listener to modify selected preference.
            checkBox.setOnCheckedChangeListener((compoundButton, bool) -> {
                if (! bool) {
                    selectedPreference.getDeactivate()
                            .add(Preference.TravelMeanEnum
                                    .getEnumFromString(checkBox.getText().toString()));
                } else {
                    selectedPreference.getDeactivate()
                            .remove(Preference.TravelMeanEnum
                                    .getEnumFromString(checkBox.getText().toString()));
                }
            });
            // Add checkbox to linear layout.
            checkBoxes_linearLayout.addView(checkBox);
        }
    }

    /**
     * Sets the right selection for the selected preference paramFirstPath.
     */
    private void setPreferredPathSpinner() {
        int index = 0;
        String param = selectedPreference.getParamFirstPath().toString();
        for (int i = 0; i < Preference.ParamFirstPath.values().length; i++) {
            if (Preference.ParamFirstPath.values()[i].toString().equals(param)) {
                index = i;
            }
        }
        preferredPath_spinner.setSelection(index);
    }

    /**
     * Sets period and distance constraints for the selected travel mean.
     */
    private void fillConstraints() {
        Preference.PeriodConstraint periodConstraint  = selectedPreference.getPeriodOfDayConstraint(selectedTravelMean);
        Preference.DistanceConstraint distanceConstraint = selectedPreference.getDistanceConstraint(selectedTravelMean);
        // Set standard constraints.
        minTime_textView.setText("00:00");
        maxTime_textView.setText("24:00");
        minDistance_editText.setText("");
        maxDistance_editText.setText("");
        // Period constraint.
        if (periodConstraint != null) {
            if (periodConstraint.getConcerns().getTravelMean().equals(selectedTravelMean)) {
                minTime_textView.setText(DateUtility.getHHmmFromSeconds(periodConstraint.getMinHour()));
                maxTime_textView.setText(DateUtility.getHHmmFromSeconds(periodConstraint.getMaxHour()));
            }
        }
        if (distanceConstraint != null) {
            if (distanceConstraint.getConcerns().getTravelMean().equals(selectedTravelMean)) {
                minDistance_editText.setText(Float.toString(distanceConstraint.getMinLength()));
                maxDistance_editText.setText(Float.toString(distanceConstraint.getMaxLength()));
            }
        }
    }

    /**
     * When the travel means constraint changes, changes gets saved.
     */
    private void saveEditedConstraints() {
        // Save distance constraint if input fields are not empty.
        if (minDistance_editText.getText().length() != 0 &&
                maxDistance_editText.getText().length() != 0) {
            Preference.DistanceConstraint distanceConstraint = selectedPreference
                    .getDistanceConstraint(selectedTravelMean);
            if (distanceConstraint != null) {
                // Constraint on travel mean already present, it must be removed.
                selectedPreference.getDistanceConstraints().remove(distanceConstraint);
            }
            // Save edited constraint.
            selectedPreference.getDistanceConstraints().add(new Preference.DistanceConstraint(
                    0,
                    Preference.TravelMeanEnum.getEnumFromString(selectedTravelMean),
                    Integer.parseInt(minDistance_editText.getText().toString()),
                    Integer.parseInt(maxDistance_editText.getText().toString())
            ));
        }
        // Save period constraint if input fields have been modified.
        if (! (minTime_textView.getText().toString().equals("00:00") &&
                maxTime_textView.getText().toString().equals("24:00"))) {
            Preference.PeriodConstraint constraint = selectedPreference.getPeriodOfDayConstraint(selectedTravelMean);
            if (constraint != null) {
                selectedPreference.getPeriodOfDayConstraints().remove(constraint);
            }
            selectedPreference.getPeriodOfDayConstraints().add(new Preference.PeriodConstraint(
                    0,
                    Preference.TravelMeanEnum.getEnumFromString(selectedTravelMean),
                    DateUtility.getSecondsFromHHmm(minTime_textView.getText().toString()),
                    DateUtility.getSecondsFromHHmm(maxTime_textView.getText().toString())
            ));
        }
    }

    private void addPreferenceToServer() {
        // Save inserted name for the new preference.
        String newPreferenceName = ((TextView) findViewById(R.id.newPreferenceName_textView))
                .getText().toString();

        if (!validate()) {
            Toast.makeText(getBaseContext(), "Something is wrong", Toast.LENGTH_LONG).show();
            return;
        }
        // Save last edited constraints.
        saveEditedConstraints();
        // Send request to server.
        waitForServerResponse();
        AddPreferenceController addPreferenceController = new AddPreferenceController(addPreferenceHandler);
        PreferenceBody preferenceBody = new PreferenceBody(
                newPreferenceName,
                selectedPreference.getParamFirstPath(),
                selectedPreference.getPeriodOfDayConstraints(),
                selectedPreference.getDistanceConstraints(),
                selectedPreference.getDeactivate()
        );
        addPreferenceController.start(token, preferenceBody);
    }

    /**
     * Checks if the user inputs are correct.
     * @return true if correct, false otherwise.
     */
    private boolean validate() {
        TextView newPreferenceName_textView = findViewById(R.id.newPreferenceName_textView);
        // Reset errors.
        newPreferenceName_textView.setError(null);

        boolean valid = true;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(newPreferenceName_textView.getText().toString())) {
            newPreferenceName_textView.setError(getString(R.string.error_field_required));
            focusView = newPreferenceName_textView;
            valid = false;
        }
        if (!valid) {
            // There was an error; focus the first form field with an error.
            focusView.requestFocus();
        }

        return valid;
    }

    private void modifyPreferenceToServer() {
        // Save edited constraints.
        saveEditedConstraints();
        // Send request to server.
        waitForServerResponse();
        ModifyPreferenceController modifyPreferenceController = new ModifyPreferenceController(modifyPreferenceHandler);
        PreferenceBody preferenceBody = new PreferenceBody(
                selectedPreference.getName(),
                selectedPreference.getParamFirstPath(),
                selectedPreference.getPeriodOfDayConstraints(),
                selectedPreference.getDistanceConstraints(),
                selectedPreference.getDeactivate()
        );
        preferenceBody.setId(selectedPreference.getId());
        modifyPreferenceController.start(token, preferenceBody);
    }

    private void deletePreferenceFromServer() {
        // Send request to server.
        waitForServerResponse();
        DeletePreferenceController deletePreferenceController = new DeletePreferenceController(deletePreferenceHandler);
        deletePreferenceController.start(token, selectedPreference.getId());
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

    public Map<String, Preference> getPreferencesMap() {
        return preferencesMap;
    }

    public Preference getSelectedPreference() {
        return selectedPreference;
    }
}