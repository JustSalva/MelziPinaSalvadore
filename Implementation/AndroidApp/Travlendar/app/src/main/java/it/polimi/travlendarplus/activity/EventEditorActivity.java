package it.polimi.travlendarplus.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.fragment.DatePickerFragment;
import it.polimi.travlendarplus.activity.fragment.TimePickerFragment;

public class EventEditorActivity extends MenuActivity {
    // UI references.
    private LinearLayout normalEvent_linearLayout;
    private LinearLayout breakEvent_linearLayout;
    // Text views.
    private AutoCompleteTextView eventName_editText;
    private TextView date_textView;
    private TextView startingTime_textView;
    private TextView endingTime_textView;
    private AutoCompleteTextView description_editText;
    private AutoCompleteTextView minimumTime_editText;
    // Spinners.
    private Spinner typeOfEvent_spinner;
    private Spinner eventLocation_spinner;
    private Spinner startTravelingAt_spinner;
    private Spinner previousLocation_spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);
        super.setupMenuToolbar();

        normalEvent_linearLayout = findViewById(R.id.normalEvent_linearLayout);
        breakEvent_linearLayout = findViewById(R.id.breakEvent_linearLayout);
        eventName_editText = findViewById(R.id.eventName_editText);
        date_textView = findViewById(R.id.date_textView);
        startingTime_textView = findViewById(R.id.startingTime_textView);
        endingTime_textView = findViewById(R.id.endingTime_textView);
        description_editText = findViewById(R.id.description_editText);
        minimumTime_editText = findViewById(R.id.minimumTime_editText);
        typeOfEvent_spinner = findViewById(R.id.typeofEvent_spinner);
        eventLocation_spinner = findViewById(R.id.eventLocation_spinner);
        startTravelingAt_spinner = findViewById(R.id.startTravelingAt_spinner);
        previousLocation_spinner = findViewById(R.id.previousLocation_spinner);
    }

    /**
     * Checks the fields to assure the all the user's inputs are valid.
     * If not, user gets notified by a toast and errors are shown.
     */
    private boolean validate() {
        // Reset errors.
        eventName_editText.setError(null);
        date_textView.setError(null);
        startingTime_textView.setError(null);
        endingTime_textView.setError(null);

        boolean valid = true;
        View focusView = null;

        // Check for a non empty name.
        if (eventName_editText.getText().length() == 0) {
            eventName_editText.setError(getString(R.string.error_field_required));
            focusView = eventName_editText;
            valid = false;
        }

        // Check for a non empty date.
        if (date_textView.getText().length() == 0) {
            date_textView.setError(getString(R.string.error_field_required));
            focusView = date_textView;
            valid = false;
        }

        // Check for a non empty starting time.
        if (startingTime_textView.getText().length() == 0) {
            startingTime_textView.setError(getString(R.string.error_field_required));
            focusView = startingTime_textView;
            valid = false;
        }

        // Check for a non empty ending time.
        if (endingTime_textView.getText().length() == 0) {
            endingTime_textView.setError(getString(R.string.error_field_required));
            focusView = endingTime_textView;
            valid = false;
        }
        // Check which radio button is selected.
        if (findViewById(R.id.normalEvent_radioButton).isSelected()) {
            // Check normal event fields.
        } else { // Check break events fields.
            // Check for a non empty minimum time.
            if (minimumTime_editText.getText().length() == 0) {
                minimumTime_editText.setError(getString(R.string.error_field_required));
                focusView = minimumTime_editText;
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
     * Shows the right fields depending on the type of event currently selected.
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.normalEvent_radioButton:
                if (checked) {
                    normalEvent_linearLayout.setVisibility(View.VISIBLE);
                    breakEvent_linearLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.breakEvent_radioButton:
                if (checked) {
                    normalEvent_linearLayout.setVisibility(View.GONE);
                    breakEvent_linearLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    /**
     * Shows the departure location field if the possibility is selected.
     */
    public void onSwitchClicked(View view) {
        boolean checked = ((Switch) view).isChecked();
        if (checked) {
            findViewById(R.id.previousLocation_linearLayout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.previousLocation_linearLayout).setVisibility(View.GONE);
        }
    }

    /**
     * Allows the user to select a date.
     * Displays the selected date in the textView adjacent to the button.
     */
    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTextView(findViewById(R.id.date_textView));
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * Allows the user to select a time.
     * Displays the selected time in the textView adjacent to the button.
     */
    public void showTimePickerDialog(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        if (view == findViewById(R.id.startingTime_button)) {
            newFragment.setTextView(findViewById(R.id.startingTime_textView));
        } else if (view== findViewById(R.id.endingTime_button)) {
            newFragment.setTextView(findViewById(R.id.endingTime_textView));
        }
        newFragment.show(getFragmentManager(), "timePicker");
    }
}
