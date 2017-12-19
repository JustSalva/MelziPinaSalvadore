package it.polimi.travlendarplus.activity;

import android.os.Bundle;

import it.polimi.travlendarplus.R;

public class ArrangeTripActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_trip);
        super.setupMenuToolbar();
    }
}
