package it.polimi.travlendarplus.activity;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.handler.ticket.GetCompatibleTicketsHandler;
import it.polimi.travlendarplus.database.entity.TravelComponent;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;
import it.polimi.travlendarplus.database.view_model.EventViewModel;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.ticket.GetCompatibleTicketsController;
import it.polimi.travlendarplus.retrofit.response.ticket.AllTicketsResponse;
import it.polimi.travlendarplus.retrofit.response.ticket.TicketResponse;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TravelTicketActivity extends MenuActivity {
    // UI references.
    private LinearLayout travelComponents_linearLayout;

    // View models.
    private UserViewModel userViewModel;
    private EventViewModel eventViewModel;
    private String token;
    private int eventId;
    // Travel components list.
    private List<TravelComponent> travelComponentList;
    private Map<Integer, List<TicketResponse>> compatibleTicketsMap;

    // Handlers for server requests.
    private Handler getCompatibleTicketsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_ticket);
        super.setupMenuToolbar();
        // Get event id passed via bundle from CalendarActivity.
        Intent intent = getIntent();
        eventId = (int) intent.getLongExtra("EVENT_ID", 0);

        // UI.
        travelComponents_linearLayout = findViewById(R.id.travelComponents_linearLayout);

        // Setup view models.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);
        // Attach listeners to view models.
        userViewModel.getUser().observe(this, user -> token = user != null ? user.getToken() : "");
        eventViewModel.getTravelComponents(eventId).observe(this, travelComponents -> {
            travelComponentList = travelComponents;
            fillTravelComponentsLayout();
        });

        // Handlers.
        getCompatibleTicketsHandler = new GetCompatibleTicketsHandler(getMainLooper(), getApplicationContext(), this);
    }


    /**
     * Creates a gridLayout containing a travelComponents information and tickets associated with them.
     */
    private void fillTravelComponentsLayout() {
        travelComponents_linearLayout.removeAllViews();
        for (TravelComponent travelComponent : travelComponentList) {
            GridLayout gridLayout = new GridLayout(getApplicationContext());
            gridLayout.setColumnCount(2);
            gridLayout.setBackground(getResources().getDrawable(R.drawable.rectangle, getTheme()));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            gridLayout.setLayoutParams(params);

            gridLayout.addView(createTextView(
                    "KM: ".concat(Float.toString(travelComponent.getLength()))
            ));
            gridLayout.addView(createTextView(
                    "MEAN: ".concat(travelComponent.getTravelMean())
            ));
            gridLayout.addView(createTextView(
                    "STARTS: ".concat(DateUtility.getHHmmFromSeconds(travelComponent.getStartTime()))
            ));
            gridLayout.addView(createTextView(
                    "ENDS: ".concat(DateUtility.getHHmmFromSeconds(travelComponent.getEndTime()))
            ));
            gridLayout.addView(createTextView(
                    "FROM: \n".concat(travelComponent.getDepartureLocation().replace(",", ",\n"))
            ));
            gridLayout.addView(createTextView(
                    "TO: \n".concat(travelComponent.getArrivalLocation().replace(",", ",\n"))
            ));
            // Button with on click load list of compatible tickets.
            if (travelComponent.getTravelMean().equals("Bus") ||
                    travelComponent.getTravelMean().equals("Subway") ||
                    travelComponent.getTravelMean().equals(""))
            gridLayout.addView(createTicketLL());
            travelComponents_linearLayout.addView(gridLayout);
        }
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
     * Creates a button to load tickets from the server compatible with the travel component.
     * @return A button.
     */
    private LinearLayout createTicketLL() {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2);
        params.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(params);
        Button button = new Button(getApplicationContext());
        button.setText("Tickets");
        button.setOnClickListener(click -> {
            // Send request to server.
            waitForServerResponse();
            GetCompatibleTicketsController getCompatibleTicketsController =
                    new GetCompatibleTicketsController(getCompatibleTicketsHandler);
            getCompatibleTicketsController.start(
                    token,
                    eventId
            );
        });
        linearLayout.addView(button);
        return linearLayout;
    }

    /**
     * Fills the compatibleTickets map.
     * @param travelComponentId Id of the travel component.
     * @param allTicketsResponse Server response with all the compatible tickets.
     */
    public void fillCompatibleTickets(int travelComponentId, AllTicketsResponse allTicketsResponse) {
        List<TicketResponse> tickets = new ArrayList<>();
        tickets.addAll(allTicketsResponse.getGenericTickets());
        tickets.addAll(allTicketsResponse.getDistanceTickets());
        tickets.addAll(allTicketsResponse.getPathTickets());
        tickets.addAll(allTicketsResponse.getPeriodTickets());
        if (! tickets.isEmpty()) {
            compatibleTicketsMap.put(travelComponentId, tickets);
        }
    }
}
