package com.shakk.travlendar.activities;

import android.os.Bundle;

import com.shakk.travlendar.R;

public class EventEditingActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editing);
        super.setupMenuToolbar();
    }
}
