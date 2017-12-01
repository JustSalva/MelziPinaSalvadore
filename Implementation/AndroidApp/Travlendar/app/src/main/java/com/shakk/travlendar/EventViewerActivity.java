package com.shakk.travlendar;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
