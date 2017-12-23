package it.polimi.travlendarplus.activity;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.handler.ticket.GetTicketsHandler;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;
import it.polimi.travlendarplus.database.view_model.TicketsViewModel;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.ticket.GetTicketsController;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Activity that shows a ticket info.
 * To be implemented.
 */
public class TicketsViewerActivity extends MenuActivity {
    // UI references.
    private LinearLayout ticketsContainer_linearLayout;
    // View models.
    private UserViewModel userViewModel;
    private TicketsViewModel ticketsViewModel;
    // Variables references.
    private List<Ticket> ticketsList;
    private boolean ticketsDownloaded = false;
    private String token;
    // Server responses handlers.
    private Handler getTicketsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_viewer);
        super.setupMenuToolbar();

        ticketsContainer_linearLayout = findViewById(R.id.ticketsContainer_linearLayout);

        // Setup view models.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        ticketsViewModel = ViewModelProviders.of(this).get(TicketsViewModel.class);
        // Attach listeners to view models.
        userViewModel.getUser().observe(this, user -> {
            token = user != null ? user.getToken() : "";
        });
        ticketsViewModel.getTickets().observe(this, tickets -> {
            ticketsList = tickets;
            fillTicketsLayout();
        });
        // Setup handlers.
        getTicketsHandler = new GetTicketsHandler(Looper.getMainLooper(), getApplicationContext(), this);

        if (! ticketsDownloaded) {
            loadTicketsFromServer();
            ticketsDownloaded = true;
        }

        findViewById(R.id.addTicket_button).setOnClickListener(
                click -> startActivity(new Intent(getApplicationContext(), TicketEditorActivity.class))
        );
    }

    private void loadTicketsFromServer() {
        // Send request to server.
        waitForServerResponse();
        GetTicketsController getTicketsController = new GetTicketsController(getTicketsHandler);
        getTicketsController.start(token);
    }

    private void fillTicketsLayout() {
        ticketsContainer_linearLayout.removeAllViews();
        for (Ticket ticket : ticketsList) {
            ticketsContainer_linearLayout.addView(insertTicketGridLayout(ticket));
        }
    }

    private GridLayout insertTicketGridLayout(Ticket ticket) {
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
            case GENERIC:
                break;
        }
        return gridLayout;
    }
}
