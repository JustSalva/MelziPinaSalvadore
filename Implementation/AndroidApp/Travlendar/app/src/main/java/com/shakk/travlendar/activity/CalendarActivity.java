package com.shakk.travlendar.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shakk.travlendar.DateUtility;
import com.shakk.travlendar.R;
import com.shakk.travlendar.activity.fragment.DatePickerFragment;
import com.shakk.travlendar.activity.tasks.InsertBreakEventsTask;
import com.shakk.travlendar.activity.tasks.InsertEventsTask;
import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.User;
import com.shakk.travlendar.database.entity.event.BreakEvent;
import com.shakk.travlendar.database.entity.event.Event;
import com.shakk.travlendar.database.entity.event.GenericEvent;
import com.shakk.travlendar.database.view_model.CalendarViewModel;
import com.shakk.travlendar.database.view_model.UserViewModel;
import com.shakk.travlendar.retrofit.controller.GetEventsController;
import com.shakk.travlendar.retrofit.response.BreakEventResponse;
import com.shakk.travlendar.retrofit.response.EventResponse;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CalendarActivity extends MenuActivity {

    private TextView date_textView;
    private RelativeLayout events_relativeLayout;

    private CalendarViewModel calendarViewModel;
    private UserViewModel userViewModel;
    private String token;
    private long timestamp;
    // Variable to check if the events have already been downloaded.
    private boolean eventsDownloaded = false;

    private Calendar calendar = new GregorianCalendar();

    private Handler getterHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        super.setupMenuToolbar();

        // Saves UI references.
        date_textView = findViewById(R.id.date_textView);
        events_relativeLayout = findViewById(R.id.events_relativeLayout);
        findViewById(R.id.addEvent_button).setOnClickListener(click -> goToEventCreation());

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
        calendarViewModel.getScheduledEvents(calendar.getTime().getTime())
                .observe(this, scheduledEvents -> {
                    fillEventsRelativeLayout(scheduledEvents);
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
            }
        });

        // Set as drop recipient for drag action.
        events_relativeLayout.setOnDragListener(new MyDragListener());

        setupGetterHandler();
    }

    private void loadEventsFromServer() {
        // Send request to server.
        waitForServerResponse();
        GetEventsController getEventsController = new GetEventsController(getterHandler);
        getEventsController.start(token, timestamp);
    }

    private void setupGetterHandler() {
        // Handle server responses.
        getterHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Toast.makeText(getBaseContext(), "No internet connection available!", Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        Toast.makeText(getBaseContext(), "Events updated!", Toast.LENGTH_LONG).show();
                        // Retrieve data from bundle.
                        Bundle bundle = msg.getData();
                        Log.d("EVENTS_JSON", bundle.getString("jsonEvents"));
                        String jsonEvents = bundle.getString("jsonEvents");
                        List<EventResponse> events = new Gson().fromJson(
                                jsonEvents,
                                new TypeToken<List<EventResponse>>(){}.getType()
                        );
                        Log.d("BREAK_EVENTS_JSON", bundle.getString("jsonBreakEvents"));
                        String jsonBreakEvents = bundle.getString("jsonBreakEvents");
                        List<BreakEventResponse> breakEvents = new Gson().fromJson(
                                jsonBreakEvents,
                                new TypeToken<List<BreakEventResponse>>(){}.getType()
                        );
                        // Write new events into DB.
                        new InsertEventsTask(getApplicationContext(), events).execute();
                        // Write new break events into DB.
                        new InsertBreakEventsTask(getApplicationContext(), breakEvents).execute();
                        // Update timestamp in DB.
                        //new UpdateTimestampTask(getApplicationContext()).execute();
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

    //TODO
    private void fillEventsRelativeLayout(List<GenericEvent> events) {
        for (GenericEvent event : events) {
            events_relativeLayout.addView(createEventTextView(event));
        }
    }

    //TODO:
    private TextView createEventTextView(GenericEvent event) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(event.getName());
        textView.setBackground(getResources().getDrawable(R.drawable.rectangle, getTheme()));

        int greenColor = Color.parseColor("#FF88CF92");
        textView.setBackgroundColor(greenColor);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, 500);
        params.setMargins(10, 10, 10, 10);
        textView.setLayoutParams(params);

        return textView;
    }

    public void goToEventCreation() {
        Intent intent = new Intent(this, EventEditorActivity.class);
        startActivity(intent);
    }

    public void goToEventViewer(View view) {
        Intent intent = new Intent(this, EventViewerActivity.class);
        startActivity(intent);
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTextView(findViewById(R.id.date_textView));
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * Disables user input fields.
     */
    private void waitForServerResponse() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    /**
     * Enables user input fields.
     */
    private void resumeNormalMode() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View viewDragged, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(viewDragged);
                viewDragged.startDrag(data, shadowBuilder, viewDragged, 0);
                viewDragged.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Listens if a View if been dragged by the user.
     */
    private final class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View viewReceiving, DragEvent event) {
            int action = event.getAction();
            View viewDragged = (View) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    viewReceiving.setBackgroundColor(Color.parseColor("#000000"));
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    viewReceiving.setBackgroundColor(Color.parseColor("#00FF00"));
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    viewReceiving.setBackgroundColor(Color.parseColor("#0000FF"));
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    ViewGroup owner = (ViewGroup) viewDragged.getParent();
                    owner.removeView(viewDragged);
                    LinearLayout container = (LinearLayout) viewReceiving;
                    container.addView(viewDragged, 0);
                    viewDragged.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    viewDragged.setVisibility(View.VISIBLE);
                default:
                    break;
            }
            return true;
        }
    }
}
