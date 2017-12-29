package it.polimi.travlendarplus.activity;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.MiniTravel;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.handler.event.SelectTravelHandler;
import it.polimi.travlendarplus.activity.handler.ticket.GetCompatibleTicketsHandler;
import it.polimi.travlendarplus.database.entity.TravelComponent;
import it.polimi.travlendarplus.database.view_model.EventViewModel;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.event.SelectTravelController;
import it.polimi.travlendarplus.retrofit.controller.ticket.GetCompatibleTicketsController;
import it.polimi.travlendarplus.retrofit.response.ticket.AllTicketsResponse;
import it.polimi.travlendarplus.retrofit.response.ticket.TicketResponse;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Activity that shows travel components of a travel and allows the user to link tickets to them.
 */
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
    private Map<Integer, LinearLayout> travelComponentsLLMap = new HashMap<>();
    private Map<Integer, Integer> travelComponentSelectedTicket;

    // Handlers for server requests.
    private Handler getCompatibleTicketsHandler;
    private Handler selectTravelHandler;

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
            travelComponentSelectedTicket = new HashMap<>();
            for (TravelComponent travelComponent : travelComponents) {
                if (travelComponent.getTicketId() != 0) {
                    travelComponentSelectedTicket.put((int) travelComponent.getId(), (int) travelComponent.getTicketId());
                }
            }
            fillTravelComponentsLayout();
        });

        // Handlers.
        getCompatibleTicketsHandler = new GetCompatibleTicketsHandler(getMainLooper(), this);
        selectTravelHandler = new SelectTravelHandler(getMainLooper(),  this);
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
                    travelComponent.getTravelMean().equals("Train") ||
                    travelComponent.getTravelMean().equals("Tram"))
            gridLayout.addView(createTicketLL(travelComponent.getId()));
            travelComponents_linearLayout.addView(gridLayout);
        }
    }

    /**
     * Creates a TextView to be inserted in the GridLayout.
     * @param content Content to be written in the TextView.
     * @return The TextView created in right way.
     */
    private LinearLayout createTextView(String content) {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(params);
        TextView textView = new TextView(getApplicationContext());
        textView.setText(content);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(textView);
        return linearLayout;
    }

    /**
     * Creates a linearLayout to load tickets from the server compatible with the travel component.
     * @return A linearLayout containing a button.
     */
    private LinearLayout createTicketLL(long travelComponentId) {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        travelComponentsLLMap.put((int) travelComponentId, linearLayout);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.height = WRAP_CONTENT;
        params.width = MATCH_PARENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2);
        params.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(params);
        // Add button that loads compatible tickets from server.
        Button button = new Button(getApplicationContext());
        button.setText("Tickets");
        button.setOnClickListener(click -> {
            // Send request to server.
            waitForServerResponse();
            GetCompatibleTicketsController getCompatibleTicketsController =
                    new GetCompatibleTicketsController(getCompatibleTicketsHandler);
            getCompatibleTicketsController.start(
                    token,
                    (int) travelComponentId
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
        // Get all ticket from response.
        compatibleTicketsMap = new HashMap<>();
        List<TicketResponse> tickets = new ArrayList<>();
        tickets.addAll(allTicketsResponse.getGenericTickets());
        tickets.addAll(allTicketsResponse.getDistanceTickets());
        tickets.addAll(allTicketsResponse.getPathTickets());
        tickets.addAll(allTicketsResponse.getPeriodTickets());
        if (! tickets.isEmpty()) {
            compatibleTicketsMap.put(travelComponentId, tickets);
        } else {
            Toast.makeText(this, "No tickets compatible!", Toast.LENGTH_LONG).show();
        }
        // Show compatible tickets.
        LinearLayout linearLayout = travelComponentsLLMap.get(travelComponentId);
        linearLayout.removeAllViews();
        for (TicketResponse ticketResponse : tickets) {
            // Create LinearLayout to display each one of them.
            LinearLayout ticketLL = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            ticketLL.setLayoutParams(params);
            LinearLayout.LayoutParams insideParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            TextView ticketName_TV = new TextView(this);
            ticketName_TV.setLayoutParams(insideParams);
            ticketName_TV.setText(ticketResponse.toString());
            ticketName_TV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ticketName_TV.setTextColor(Color.parseColor("#000000"));
            ticketLL.addView(ticketName_TV);
            // Check if the ticket is already selected.
            if (! travelComponentSelectedTicket.containsKey(travelComponentId)) {
                // Add select button.
                Button selectButton = new Button(this);
                selectButton.setText("Select");
                selectButton.setOnClickListener(click -> {
                    // Send request to server.
                    waitForServerResponse();
                    SelectTravelController selectTravelController =
                            new SelectTravelController(selectTravelHandler);
                    selectTravelController.selectTravel(
                            token,
                            (int) ticketResponse.getId(),
                            travelComponentId
                    );
                });
                selectButton.setLayoutParams(insideParams);
                ticketLL.addView(selectButton);
            } else if (ticketResponse.getId() == travelComponentSelectedTicket.get(travelComponentId)){
                ticketName_TV.setTypeface(null, Typeface.BOLD);
                // Add deselect button.
                Button deselectButton = new Button(this);
                deselectButton.setText("Deselect");
                deselectButton.setOnClickListener(click -> {
                    // Send request to server.
                    waitForServerResponse();
                    SelectTravelController selectTravelController =
                            new SelectTravelController(selectTravelHandler);
                    selectTravelController.deselectTravel(
                            token,
                            (int) ticketResponse.getId(),
                            travelComponentId
                    );
                });
                deselectButton.setLayoutParams(insideParams);
                ticketLL.addView(deselectButton);
            }
            // Add to main layout.
            linearLayout.addView(ticketLL);
        }
    }
}
