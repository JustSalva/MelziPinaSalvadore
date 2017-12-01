package com.shakk.travlendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ArrangeTripActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_trip);
        super.setupMenuToolbar();
    }
}
