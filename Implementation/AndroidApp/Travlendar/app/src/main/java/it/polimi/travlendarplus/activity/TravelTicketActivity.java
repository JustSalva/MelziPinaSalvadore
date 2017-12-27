package it.polimi.travlendarplus.activity;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.database.entity.TravelComponent;
import it.polimi.travlendarplus.database.view_model.EventViewModel;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TravelTicketActivity extends MenuActivity {
    // UI references.
    private LinearLayout travelComponents_linearLayout;

    // View models.
    private EventViewModel eventViewModel;
    private int eventId;
    // Travel components list.
    private List<TravelComponent> travelComponentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_ticket);
        super.setupMenuToolbar();
        // Get event id passed via bundle from CalendarActivity.
        Intent intent = getIntent();
        eventId = (int) intent.getLongExtra("EVENT_ID", 0);

        travelComponents_linearLayout = findViewById(R.id.travelComponents_linearLayout);

        // Setup view models.
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);
        // Attach listeners to view models.
        eventViewModel.getTravelComponents(eventId).observe(this, travelComponents -> {
            travelComponentList = travelComponents;
            fillTravelComponentsLayout();
        });
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

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = MATCH_PARENT;
            params.height = WRAP_CONTENT;
            params.setMargins(10, 10, 10, 10);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            gridLayout.setLayoutParams(params);

            gridLayout.addView(createTextView(Float.toString(travelComponent.getLength())));
            gridLayout.addView(createTextView(travelComponent.getTravelMean()));
            gridLayout.addView(createTextView(DateUtility.getHHmmFromSeconds(travelComponent.getStartTime())));
            gridLayout.addView(createTextView(DateUtility.getHHmmFromSeconds(travelComponent.getEndTime())));
            gridLayout.addView(createTextView(travelComponent.getDepartureLocation()));
            gridLayout.addView(createTextView(travelComponent.getArrivalLocation()));
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
        textView.setElegantTextHeight(true);
        textView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textView.setSingleLine(false);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        textView.setLayoutParams(params);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return textView;
    }

}
