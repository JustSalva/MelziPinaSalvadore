package it.polimi.travlendarplus.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.fragment.DatePickerFragment;
import it.polimi.travlendarplus.activity.handler.event.DeleteEventHandler;
import it.polimi.travlendarplus.activity.handler.event.GetEventsHandler;
import it.polimi.travlendarplus.activity.handler.event.ScheduleEventHandler;
import it.polimi.travlendarplus.activity.listener.DragToDeleteListener;
import it.polimi.travlendarplus.activity.listener.DragToScheduleListener;
import it.polimi.travlendarplus.activity.listener.MyTouchListener;
import it.polimi.travlendarplus.database.entity.TravelComponent;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.database.view_model.CalendarViewModel;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.GetEventsController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Activity that displays the events in the database.
 * Allows the user to schedule and delete events by dragging them.
 */
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

    private Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    private List<GenericEvent> eventsList = new ArrayList<>();
    private List<TravelComponent> travelComponentsList = new ArrayList<>();
    // Handlers for server responses.
    private Handler getEventsHandler;
    private Handler deleteEventHandler;
    private Handler scheduleEventHandler;

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
        calendarViewModel.getAllTravelComponents().observe(
                this, travelComponents -> {
                    travelComponentsList = travelComponents;
                    fillEventsRelativeLayout();
                }
        );

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

        // Check if the date is already set.
        if (date_textView.getText().length() == 0) {
            // Set today's date as date.
            calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            String dateString = DateUtility.getStringFromCalendar(calendar);
            date_textView.setText(dateString);
        }

        // Set events relativeLayout as drop recipient for drag action to schedule.
        events_relativeLayout.setOnDragListener(new DragToScheduleListener(getApplicationContext(), this));
        // Set date textView as drop recipient for drag action to delete.
        date_textView.setOnDragListener(new DragToDeleteListener(getApplicationContext(), this));
        // Set handlers.
        getEventsHandler = new GetEventsHandler(Looper.getMainLooper(), getApplicationContext(), this);
        deleteEventHandler = new DeleteEventHandler(Looper.getMainLooper(), getApplicationContext(), this);
        scheduleEventHandler = new ScheduleEventHandler(Looper.getMainLooper(), getApplicationContext(), this);
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
        long selectedDate = (calendar.getTimeInMillis() / 1000) + 3600;
        // Inserts break events.
        for (GenericEvent event : eventsList) {
            if (event.getType().equals(GenericEvent.EventType.BREAK) && (selectedDate == event.getDate())) {
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
            if (event.getType().equals(GenericEvent.EventType.EVENT) && (selectedDate == event.getDate())) {
                if (event.isScheduled()) {
                    // Insert scheduled events.
                    events_relativeLayout.addView(createEventTextView(event));
                    // Inserts travels for scheduled events.
                    events_relativeLayout.addView(createTravelTextView(event));
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
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setId((int) event.getId());
        textView = setColorTextView(textView, event);
        textView = setDimensionTextView(textView, event);
        // Add on touch listener to events.
        textView.setOnTouchListener(new MyTouchListener());
        return textView;
    }

    /**
     * Creates and return a textView containing the travel information.
     * @param event The event related to the travel to be displayed in the textView.
     * @return A textView containing the travel info.
     */
    private TextView createTravelTextView(GenericEvent event) {
        List<TravelComponent> eventTravelComponents = new ArrayList<>();
        for (TravelComponent travelComponent : travelComponentsList) {
            if (travelComponent.getEventId() == event.getId()) {
                eventTravelComponents.add(travelComponent);
            }
        }
        // Saves starting and ending travel time.
        long startingTime = Long.MAX_VALUE;
        long endingTime = 0;
        for (TravelComponent travelComponent : eventTravelComponents) {
            if (travelComponent.getEndTime() > endingTime) {
                endingTime = travelComponent.getEndTime();
            }
            if (travelComponent.getStartTime() < startingTime) {
                startingTime = travelComponent.getStartTime();
            }
        }
        // Set style.
        TextView textView = new TextView(getApplicationContext());
        textView.setText("Travel");
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTypeface(null, Typeface.BOLD);
        // Set colors.
        textView.setBackground(getResources().getDrawable(R.drawable.rectangle, getTheme()));
        textView.setBackgroundColor(Color.parseColor("#ADFF2F"));
        textView = setDimensionTextView(textView, event);
        // Set the height depending on the event duration.
        int minDuration = (int) (endingTime - startingTime) / 60;
        int height = minDuration * 2;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, height);
        // Sets the top margin depending on the starting time of the event.
        int minutesOfDay = (((int) startingTime) % 86400) / 60;
        int marginTop = minutesOfDay * 2;
        params.setMargins(30, marginTop, 30, 10);
        textView.setLayoutParams(params);
        // Add on touch listener to events.
        textView.setOnTouchListener(new MyTouchListener());
        return textView;
    }

    /**
     * Sets the right colors for the event textView, depending on scheduling and type of event.
     * @param textView TextView to be modified.
     * @param event Event linked to the TextView.
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

    /**
     * Sets the right dimension for the event textView, depending on duration and type of event.
     * @param textView TextView to be modified.
     * @param event Event linked to the TextView.
     * @return Modified textView.
     */
    private TextView setDimensionTextView(TextView textView, GenericEvent event) {
        // Set the height depending on the event duration.
        int minDuration = (int) (event.getEndTime() - event.getStartTime()) / 60;
        int height = minDuration * 2;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, height);
        // Sets the top margin depending on the starting time of the event.
        int minutesOfDay = (((int) event.getStartTime()) % 86400) / 60;
        int marginTop = minutesOfDay * 2;
        // Set margins depending on the type of event.
        int marginSide = event.getType() == GenericEvent.EventType.EVENT ? 30 : 10;
        params.setMargins(marginSide, marginTop, marginSide, 10);
        textView.setLayoutParams(params);
        return textView;
    }

    /**
     * Shows a datePicker fragment to select a date.
     */
    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTextView(findViewById(R.id.date_textView));
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public String getToken() {
        return token;
    }

    public Handler getDeleteEventHandler() {
        return deleteEventHandler;
    }

    public Handler getScheduleEventHandler() {
        return scheduleEventHandler;
    }
}
