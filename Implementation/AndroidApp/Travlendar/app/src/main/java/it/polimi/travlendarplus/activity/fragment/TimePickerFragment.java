package it.polimi.travlendarplus.activity.fragment;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

/**
 * Time Picker Fragment that allows the user to pick a time.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    // TextView to be modified.
    private TextView textView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Set the time in the textView.
        String chosenTime = String.format(Locale.ENGLISH, "%02d:%02d", hourOfDay, minute);
        textView.setText(chosenTime);
    }

    /**
     * Sets the textView text.
     * @param textView TextView to be modified.
     */
    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
