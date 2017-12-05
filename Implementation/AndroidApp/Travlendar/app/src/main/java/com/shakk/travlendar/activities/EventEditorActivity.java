package com.shakk.travlendar.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;

import com.shakk.travlendar.R;

public class EventEditorActivity extends MenuActivity {

    private LinearLayout normalEvent_linearLayout;
    private LinearLayout breakEvent_linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);
        super.setupMenuToolbar();

        normalEvent_linearLayout = findViewById(R.id.normalEvent_linearLayout);
        breakEvent_linearLayout = findViewById(R.id.breakEvent_linearLayout);

        ((RadioButton) findViewById(R.id.normalEvent_radioButton)).setChecked(true);
        ((Switch) findViewById(R.id.previousLocation_switch)).setChecked(false);
    }

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

    public void onSwitchClicked(View view) {
        boolean checked = ((Switch) view).isChecked();

        if (checked) {
            findViewById(R.id.previous_location_textInputLayout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.previous_location_textInputLayout).setVisibility(View.GONE);
        }
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTextView(findViewById(R.id.date_textView));
        newFragment.show(getFragmentManager(), "datePicker");
    }

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
