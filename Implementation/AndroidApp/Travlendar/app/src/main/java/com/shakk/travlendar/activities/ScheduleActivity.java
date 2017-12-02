package com.shakk.travlendar.activities;

import android.os.Bundle;

import com.shakk.travlendar.R;

public class ScheduleActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        super.setupMenuToolbar();
    }
}
