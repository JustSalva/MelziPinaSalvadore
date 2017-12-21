package it.polimi.travlendarplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.database.view_model.EventViewModel;

/**
 * Activity that allows the user to see an event info.
 * To be implemented.
 */
public class EventViewerActivity extends MenuActivity {

    private EventViewModel eventViewModel;

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
