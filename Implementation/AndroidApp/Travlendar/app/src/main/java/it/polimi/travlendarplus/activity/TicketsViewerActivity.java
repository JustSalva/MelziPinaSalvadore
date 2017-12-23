package it.polimi.travlendarplus.activity;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import it.polimi.travlendarplus.DateUtility;
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
        userViewModel.getUser().observe(this, user -> token = user != null ? user.getToken() : "");
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

    /**
     * Sends request to get tickets from the server.
     */
    private void loadTicketsFromServer() {
        // Send request to server.
        waitForServerResponse();
        GetTicketsController getTicketsController = new GetTicketsController(getTicketsHandler);
        getTicketsController.start(token);
    }

    /**
     * Clears the layout, then adds views to it representing tickets.
     */
    private void fillTicketsLayout() {
        ticketsContainer_linearLayout.removeAllViews();
        for (Ticket ticket : ticketsList) {
            ticketsContainer_linearLayout.addView(insertTicketGridLayout(ticket));
        }
    }

    /**
     * Returns a GridLayout containing all the ticket's information.
     * @param ticket Ticket with the information.
     * @return GridLayout containing ticket information.
     */
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
        gridLayout.addView(createTextView(ticket.getType().getType().concat(" ticket")));
        gridLayout.addView(createTextView("Cost: ".concat(Float.toString(ticket.getCost()))));

        switch (ticket.getType()) {
            case GENERIC:
                insertGenericTicketFields(gridLayout, ticket);
                break;
            case DISTANCE:
                insertDistanceTicketFields(gridLayout, ticket);
                break;
            case PATH:
                insertGenericTicketFields(gridLayout, ticket);
                insertPathTicketFields(gridLayout, ticket);
                break;
            case PERIOD:
                insertPeriodTicketFields(gridLayout, ticket);
                switch (ticket.getPeriodTicket().getDecoratorType()) {
                    case GENERIC:
                        insertGenericTicketFields(gridLayout, ticket);
                        break;
                    case DISTANCE:
                        insertDistanceTicketFields(gridLayout, ticket);
                        break;
                    case PATH:
                        insertGenericTicketFields(gridLayout, ticket);
                        insertPathTicketFields(gridLayout, ticket);
                        break;
                    default:
                        break;
                }
                break;
        }
        return gridLayout;
    }

    /**
     * Creates a TextView to be inserted in the GridLayout.
     * @param content Content to be written in the TextView.
     * @return The TextView created in right way.
     */
    private TextView createTextView(String content) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(content);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        textView.setLayoutParams(params);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return textView;
    }

    /**
     * Inserts the generic tickets details in the GridLayout.
     * @param gridLayout GridLayout parent of the textViews to be inserted.
     * @param ticket Ticket containing details to be inserted.
     */
    private void insertGenericTicketFields(GridLayout gridLayout, Ticket ticket) {
        String lineName = ticket.getGenericTicket().getLineName();
        gridLayout.addView(createTextView(lineName));
    }

    /**
     * Inserts the distance tickets details in the GridLayout.
     * @param gridLayout GridLayout parent of the textViews to be inserted.
     * @param ticket Ticket containing details to be inserted.
     */
    private void insertDistanceTicketFields(GridLayout gridLayout, Ticket ticket) {
        String distance = Float.toString(ticket.getDistanceTicket().getDistance());
        gridLayout.addView(createTextView(distance.concat(" km")));
    }

    /**
     * Inserts the path tickets details in the GridLayout.
     * @param gridLayout GridLayout parent of the textViews to be inserted.
     * @param ticket Ticket containing details to be inserted.
     */
    private void insertPathTicketFields(GridLayout gridLayout, Ticket ticket) {
        String startLocation = ticket.getGenericTicket().getPathTicket().getArrivalLocation();
        String endLocation = ticket.getGenericTicket().getPathTicket().getDepartureLocation();
        gridLayout.addView(createTextView("From: ".concat(startLocation)));
        gridLayout.addView(createTextView("To: ".concat(endLocation)));
    }

    /**
     * Inserts the period tickets details in the GridLayout.
     * @param gridLayout GridLayout parent of the textViews to be inserted.
     * @param ticket Ticket containing details to be inserted.
     */
    private void insertPeriodTicketFields(GridLayout gridLayout, Ticket ticket) {
        String name = ticket.getPeriodTicket().getName();
        gridLayout.addView(createTextView(
                "Start: ".concat(
                        DateUtility.getInstantFromSeconds(ticket.getPeriodTicket().getStartDate()).substring(0, 10)
                )
        ));
        gridLayout.addView(createTextView(
                "End: ".concat(
                        DateUtility.getInstantFromSeconds(ticket.getPeriodTicket().getEndDate()).substring(0, 10)
                )
        ));
        gridLayout.addView(createTextView(name));
    }
}
