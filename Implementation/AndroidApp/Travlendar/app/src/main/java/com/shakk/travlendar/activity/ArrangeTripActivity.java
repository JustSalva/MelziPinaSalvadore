package com.shakk.travlendar.activity;

import android.os.Bundle;

import com.shakk.travlendar.R;

public class ArrangeTripActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_trip);
        super.setupMenuToolbar();
    }
}
