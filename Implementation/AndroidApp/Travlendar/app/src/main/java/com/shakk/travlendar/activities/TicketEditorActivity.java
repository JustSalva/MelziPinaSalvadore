package com.shakk.travlendar.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.shakk.travlendar.R;

public class TicketEditorActivity extends MenuActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_editor);
        super.setupMenuToolbar();

        ((RadioButton) findViewById(R.id.generalTicket_radioButton)).setChecked(true);
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.generalTicket_radioButton:
                if (checked) {
                    findViewById(R.id.generalTicket_linearLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.distanceTicket_linearLayout).setVisibility(View.GONE);
                    findViewById(R.id.periodTicket_linearLayout).setVisibility(View.GONE);
                }
                break;
            case R.id.distanceTicket_radioButton:
                if (checked) {
                    findViewById(R.id.generalTicket_linearLayout).setVisibility(View.GONE);
                    findViewById(R.id.distanceTicket_linearLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.periodTicket_linearLayout).setVisibility(View.GONE);
                }
                break;
            case R.id.periodTicket_radioButton:
                if (checked) {
                    findViewById(R.id.generalTicket_linearLayout).setVisibility(View.GONE);
                    findViewById(R.id.distanceTicket_linearLayout).setVisibility(View.GONE);
                    findViewById(R.id.periodTicket_linearLayout).setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
