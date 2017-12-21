package it.polimi.travlendarplus.activity;


import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Activity that shows a ticket info.
 * To be implemented.
 */
public class TicketsViewerActivity extends MenuActivity {

    private LinearLayout ticketsContainer_linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_viewer);
        super.setupMenuToolbar();

        ticketsContainer_linearLayout = findViewById(R.id.ticketsContainer_linearLayout);
    }

    private void insertTicketGridLayout(Ticket ticket) {
        GridLayout gridLayout = new GridLayout(getApplicationContext());
        gridLayout.setColumnCount(2);
        gridLayout.setBackground(getResources().getDrawable(R.drawable.rectangle, getTheme()));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = MATCH_PARENT;
        params.height = WRAP_CONTENT;
        params.setMargins(10, 10, 10, 10);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        gridLayout.setLayoutParams(params);

        TextView cost_textView = new TextView(getApplicationContext());
        cost_textView.setText(ticket.getType().getType());
        gridLayout.addView(cost_textView);

        //TODO
        switch (ticket.getType()) {
            case GENERAL:
                break;
        }
    }
}
