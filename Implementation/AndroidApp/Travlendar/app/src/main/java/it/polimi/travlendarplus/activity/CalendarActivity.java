package it.polimi.travlendarplus.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
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
import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.database.view_model.CalendarViewModel;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.GetEventsController;

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

    private Handler getEventsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        super.setupMenuToolbar();

        // Saves UI references.
        date_textView = findViewById(R.id.date_textView);
        events_relativeLayout = findViewById(R.id.events_relativeLayout);
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
        getEventsHandler = new GetEventsHandler(Looper.getMainLooper(), getApplicationContext(), this);
    }

    private void loadEventsFromServer() {
        // Send request to server.
        waitForServerResponse();
        GetEventsController getEventsController = new GetEventsController(getEventsHandler);
        getEventsController.start(token, timestamp);
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
    public void waitForServerResponse() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    /**
     * Enables user input fields.
     */
    public void resumeNormalMode() {
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
