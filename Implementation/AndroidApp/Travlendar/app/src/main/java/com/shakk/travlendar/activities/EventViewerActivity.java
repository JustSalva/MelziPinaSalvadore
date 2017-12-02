package com.shakk.travlendar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shakk.travlendar.R;

public class EventViewerActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_viewer);
        super.setupMenuToolbar();
    }

    public void goToArrangeTrip(View view) {
        Intent intent = new Intent(this, ArrangeTripActivity.class);
        startActivity(intent);
    }
}
