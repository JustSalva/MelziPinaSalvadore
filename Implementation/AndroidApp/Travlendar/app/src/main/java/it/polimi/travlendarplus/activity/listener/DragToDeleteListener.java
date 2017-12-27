package it.polimi.travlendarplus.activity.listener;


import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.MenuActivity;
import it.polimi.travlendarplus.activity.TicketsViewerActivity;
import it.polimi.travlendarplus.retrofit.controller.event.DeleteEventController;
import it.polimi.travlendarplus.retrofit.controller.ticket.DeleteTicketController;

/**
 * Listens if a View is been dragged by the user to delete the event related.
 */
public final class DragToDeleteListener implements View.OnDragListener {

    private Context context;
    private MenuActivity activity;

    public DragToDeleteListener(Context context, MenuActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public boolean onDrag(View viewReceiving, DragEvent event) {
        int action = event.getAction();
        int color = viewReceiving.getDrawingCacheBackgroundColor();
        View viewDragged = (View) event.getLocalState();
        int id = viewDragged.getId();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                viewReceiving.setVisibility(View.VISIBLE);
                viewReceiving.setBackgroundColor(Color.parseColor("#FF0000"));
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                viewReceiving.setBackgroundColor(Color.parseColor("#FF0000"));
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                viewReceiving.setBackgroundColor(color);
                break;
            case DragEvent.ACTION_DROP:
                // Send request to server to delete something.
                if (activity instanceof CalendarActivity) {
                    // Delete event.
                    CalendarActivity calendarActivity = (CalendarActivity) activity;
                    activity.waitForServerResponse();
                    DeleteEventController deleteEventController =
                            new DeleteEventController(calendarActivity.getDeleteEventHandler());
                    deleteEventController.start(calendarActivity.getToken(), id);
                } else if (activity instanceof TicketsViewerActivity) {
                    // Delete ticket.
                    TicketsViewerActivity ticketsViewerActivity = (TicketsViewerActivity) activity;
                    activity.waitForServerResponse();
                    DeleteTicketController deleteTicketController =
                            new DeleteTicketController(ticketsViewerActivity.getDeleteTicketHandler());
                    deleteTicketController.start(ticketsViewerActivity.getToken(), id);
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                viewReceiving.setBackgroundColor(color);
                viewReceiving.setVisibility(View.GONE);
                viewDragged.setVisibility(View.VISIBLE);
            default:
                break;
        }
        return true;
    }
}
