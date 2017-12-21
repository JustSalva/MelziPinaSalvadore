package it.polimi.travlendarplus.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.fragment.DatePickerFragment;
import it.polimi.travlendarplus.activity.handler.GetEventsHandler;
import it.polimi.travlendarplus.activity.listener.MyDragListener;
import it.polimi.travlendarplus.database.entity.event.BreakEvent;
import it.polimi.travlendarplus.database.entity.event.Event;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.database.view_model.CalendarViewModel;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.GetEventsController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CalendarActivity extends MenuActivity {
    // UI references,
    private TextView date_textView;
    private RelativeLayout hours_relativeLayout;
    private RelativeLayout events_relativeLayout;
    private RelativeLayout overlappingEvents_relativeLayout;

    private CalendarViewModel calendarViewModel;
    private UserViewModel userViewModel;
    private String token;
    private long timestamp;
    // Variable to check if the events have already been downloaded.
    private boolean eventsDownloaded = false;

    private Calendar calendar = new GregorianCalendar();
    private List<GenericEvent> eventsList = new ArrayList<>();

    private Handler getEventsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        super.setupMenuToolbar();

        // Saves UI references.
        date_textView = findViewById(R.id.date_textView);
        hours_relativeLayout = findViewById(R.id.hours_relativeLayout);
        fillHoursRelativeLayout();
        events_relativeLayout = findViewById(R.id.events_relativeLayout);
        overlappingEvents_relativeLayout = findViewById(R.id.overlappingEvents_relativeLayout);
        findViewById(R.id.addEvent_button).setOnClickListener(
                click -> startActivity(new Intent(this, EventEditorActivity.class))
        );

        // Observe token and timestamp values.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, user -> {
            token = user != null ? user.getToken() : "";
            timestamp = user != null ? user.getTimestamp() : 0;
            // To be called only on the first onCreate().
            if (! eventsDownloaded) {
                loadEventsFromServer();
                eventsDownloaded = true;
            }
        });

        // Observe events available in the selected date.
        calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
        calendarViewModel.getAllEvents().observe(
                this, events -> {
                    eventsList = events;
                    fillEventsRelativeLayout();
                });

        //overlapping_gridLayout.setOnTouchListener(new MyTouchListener());

        // Check if the date is already set.
        if (date_textView.getText().length() == 0) {
            // Set today's date as date.
            calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            String dateString = DateUtility.getStringFromCalendar(calendar);
            date_textView.setText(dateString);
        }

        date_textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing happens.
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing happens.
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // Update calendar.
                calendar = DateUtility.getCalendarFromString(date_textView.getText().toString());
                fillEventsRelativeLayout();
            }
        });

        // Set as drop recipient for drag action.
        events_relativeLayout.setOnDragListener(new MyDragListener());
        getEventsHandler = new GetEventsHandler(Looper.getMainLooper(), getApplicationContext(), this);
    }

    /**
     * Downloads and saves in the DB new events.
     */
    private void loadEventsFromServer() {
        // Send request to server.
        waitForServerResponse();
        GetEventsController getEventsController = new GetEventsController(getEventsHandler);
        getEventsController.start(token, timestamp);
    }

    /**
     * Inserts 24 hours textViews indicators in the hours_relativeLayout.
     */
    private void fillHoursRelativeLayout() {
        hours_relativeLayout.removeAllViews();
        for (int i = 0; i < 24; i++) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText(Integer.toString(i));
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, 200);
            params.setMargins(0, i*60*2, 0, 0);
            textView.setLayoutParams(params);
            hours_relativeLayout.addView(textView);
        }
    }

    /**
     * Fills relative layouts with events, checking if they are scheduled or not.
     * Breaks are inserted before regular events to make sure that the latter are on top.
     */
    private void fillEventsRelativeLayout() {
        // Clears the relative layouts of the past views.
        events_relativeLayout.removeAllViews();
        overlappingEvents_relativeLayout.removeAllViews();
        // Get long representing selected date.
        String selectedDate = date_textView.getText().toString();
        // Inserts break events.
        for (GenericEvent event : eventsList) {
            if (event.getType().equals(GenericEvent.EventType.BREAK) && selectedDate.equals(event.getDate())) {
                if (event.isScheduled()) {
                    // Insert scheduled breaks.
                    events_relativeLayout.addView(createEventTextView(event));
                } else {
                    // Insert overlapping breaks.
                    overlappingEvents_relativeLayout.addView(createEventTextView(event));
                }
            }
        }
        // Inserts regular events.
        for (GenericEvent event : eventsList) {
            if (event.getType().equals(GenericEvent.EventType.EVENT) && selectedDate.equals(event.getDate())) {
                if (event.isScheduled()) {
                    // Insert scheduled events.
                    events_relativeLayout.addView(createEventTextView(event));
                } else {
                    // Insert overlapping events.
                    overlappingEvents_relativeLayout.addView(createEventTextView(event));
                }
            }
        }
    }


    /**
     * Creates and return a textView containing the event information.
     * @param event The event to be displayed in the textView.
     * @return A textView containing the event info.
     */
    private TextView createEventTextView(GenericEvent event) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(event.getName());
        textView = setColorTextView(textView, event);
        // Set the height depending on the event duration.
        int minDuration = (int) (event.getEndTime()-event.getStartTime()) / 60;
        int height = minDuration * 2;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, height);
        // Sets the top margin depending on the starting time of the event.
        int minutesOfDay = (((int) event.getStartTime()) % 86400) / 60;
        int marginTop = minutesOfDay * 2;
        params.setMargins(10, marginTop, 10, 10);
        textView.setLayoutParams(params);
        return textView;
    }


    /**
     * Sets the right colors for the event textView, depending on scheduling and type of event.
     * @param textView TextView to be modified.
     * @param event Event linked to the textView.
     * @return Modified textView.
     */
    private TextView setColorTextView(TextView textView, GenericEvent event) {
        textView.setBackground(getResources().getDrawable(R.drawable.rectangle, getTheme()));
        int color = 0;
        // Check type of event.
        if (event.getType().equals(GenericEvent.EventType.EVENT)) {
            if (event.isScheduled()) {
                // Sets the background color green.
                color = Color.parseColor("#FF88CF92");
            } else {
                // Sets the background color red.
                color = Color.parseColor("#FF0000");
            }
        } else if (event.getType().equals(GenericEvent.EventType.BREAK)) {
            if (event.isScheduled()) {
                // Set the background color yellow.
                color = Color.parseColor("#FFFF00");
            } else {
                // Set the background color orange.
                color = Color.parseColor("#FFA500");
            }
        }
        textView.setBackgroundColor(color);
        return textView;
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTextView(findViewById(R.id.date_textView));
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
