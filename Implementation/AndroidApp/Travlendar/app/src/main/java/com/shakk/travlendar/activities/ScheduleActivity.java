package com.shakk.travlendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ScheduleActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        super.setupMenuToolbar();
    }
}
