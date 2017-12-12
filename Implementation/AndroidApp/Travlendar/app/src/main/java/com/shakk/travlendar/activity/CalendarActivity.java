package com.shakk.travlendar.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shakk.travlendar.DateUtility;
import com.shakk.travlendar.R;
import com.shakk.travlendar.activity.fragment.DatePickerFragment;
import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.event.Event;
import com.shakk.travlendar.database.entity.event.GenericEvent;
import com.shakk.travlendar.database.view_model.CalendarViewModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CalendarActivity extends MenuActivity {

    private TextView date_textView;
    private Button addEvent_button;
    private RelativeLayout events_relativeLayout;

    private CalendarViewModel calendarViewModel;

    private long dateOfCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        super.setupMenuToolbar();

        // Saves UI references.
        date_textView = findViewById(R.id.date_textView);
        addEvent_button = findViewById(R.id.addEvent_button);
        events_relativeLayout = findViewById(R.id.events_relativeLayout);

        // TODO: to be removed when downloaded events from server.
        new InsertEventsTask(getApplicationContext()).execute();

        //overlapping_gridLayout.setOnTouchListener(new MyTouchListener());

        // TODO: set date calendar right
        // Set as drop recipient for drag action.
        events_relativeLayout.setOnDragListener(new MyDragListener());

        // Set today's date in the title.
        date_textView.setText(DateUtility.getStringFromDate(new Date()));
        //dateOfCalendar = new Instant;

        calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
        calendarViewModel.getScheduledEvents(dateOfCalendar)
                .observe(this, scheduledEvents -> {
            fillEventsRelativeLayout(scheduledEvents);
        });

        addEvent_button.setOnClickListener(click -> goToEventCreation());
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

    private static class InsertEventsTask extends AsyncTask<Void, Void, Void> {

        private AppDatabase database;

        InsertEventsTask(Context context) {
            this.database = AppDatabase.getInstance(context);
        }

        protected Void doInBackground(Void... users) {
            database.calendarDao().deleteAll();
            GenericEvent genericEvent = new GenericEvent(1, "name",
                    DateUtility.getDateFromString("2017-12-11"), 1100, 1200, true);
            Event event = new Event("de", "meeting", "qua",
                    false, null);
            genericEvent.setType(GenericEvent.EventType.EVENT);
            genericEvent.setEvent(event);
            database.calendarDao().insert(genericEvent);
            return null;
        }
    }
}
