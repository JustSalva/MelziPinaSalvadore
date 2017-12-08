package com.shakk.travlendar.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shakk.travlendar.DateUtility;
import com.shakk.travlendar.R;
import com.shakk.travlendar.activity.fragment.DatePickerFragment;
import com.shakk.travlendar.database.view_model.CalendarViewModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CalendarActivity extends MenuActivity {

    private CalendarViewModel calendarViewModel;

    private TextView date_textView;
    private LinearLayout calendar_linearLayout;
    private GridLayout overlapping_gridLayout;

    private long dateOfCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        super.setupMenuToolbar();

        date_textView = findViewById(R.id.date_textView);
        calendar_linearLayout = findViewById(R.id.calendar_linearLayout);
        overlapping_gridLayout = findViewById(R.id.overlapping_gridLayout);

        overlapping_gridLayout.setOnTouchListener(new MyTouchListener());
        findViewById(R.id.calendar_linearLayout).setOnDragListener(new MyDragListener());


        dateOfCalendar = DateUtility.getDateFromString(date_textView.toString());

        calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
        calendarViewModel.getEvents(dateOfCalendar).observe(this, user -> {
            fillCalendarGridLayout();
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO
    private void fillCalendarGridLayout() {
        //calendar_gridLayout.addView(createEventGridLayout(true),1);
    }

    //TODO: create cycle filling the gridLayout
    private GridLayout createEventGridLayout(boolean isScheduled) {
        GridLayout gridLayout = new GridLayout(getApplicationContext());
        gridLayout.setBackground(getResources().getDrawable(R.drawable.rectangle, getTheme()));

        int color = isScheduled ? Color.parseColor("#FF88CF92") : Color.parseColor("#00000000");
        gridLayout.setBackgroundColor(color);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = WRAP_CONTENT;
        params.setMargins(10, 10, 10, 10);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        gridLayout.setLayoutParams(params);

        return gridLayout;
    }

    //TODO:fill the eventGridLayout
    private void fillEventGridLayout(GridLayout eventGridLayout) {
        TextView textView = new TextView(getApplicationContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        textView.setLayoutParams(params);


    }

    public void goToEventCreation(View view) {
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
}
