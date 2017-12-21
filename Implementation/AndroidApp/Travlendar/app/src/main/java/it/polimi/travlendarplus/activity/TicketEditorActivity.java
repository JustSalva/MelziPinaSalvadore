package it.polimi.travlendarplus.activity;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import it.polimi.travlendarplus.R;

/**
 * Activity that allows the creation and editing of a ticket.
 * To be implemented.
 */
public class TicketEditorActivity extends MenuActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_editor);
        super.setupMenuToolbar();

        ((RadioButton) findViewById(R.id.generalTicket_radioButton)).setChecked(true);
    }

    /**
     * When a radio button is clicked, depending on the ticket type
     * selection a different layout get shown and the other ones gets hidden.
     */
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
