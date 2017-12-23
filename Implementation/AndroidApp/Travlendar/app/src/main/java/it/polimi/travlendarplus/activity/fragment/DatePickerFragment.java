package it.polimi.travlendarplus.activity.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Date Picker Fragment that allows the user to pick a date.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    // TextView to be modified.
    private TextView textView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Set the date in the textView.
        String chosenDate = String.format(Locale.ENGLISH, "%02d-%02d-%02d", year, month+1, day);
        textView.setText(chosenDate);
    }


    /**
     * Sets the textView text.
     * @param textView TextView to be modified.
     */
    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
