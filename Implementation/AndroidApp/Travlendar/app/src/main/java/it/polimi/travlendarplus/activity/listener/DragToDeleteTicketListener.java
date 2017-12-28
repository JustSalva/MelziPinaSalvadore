package it.polimi.travlendarplus.activity.listener;


import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;

import it.polimi.travlendarplus.activity.TicketsViewerActivity;

/**
 * Listens if a View is been dragged by the user to delete the ticket related.
 */
public final class DragToDeleteTicketListener implements View.OnDragListener {

    private TicketsViewerActivity activity;

    public DragToDeleteTicketListener(TicketsViewerActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onDrag(View viewReceiving, DragEvent event) {
        int color = viewReceiving.getDrawingCacheBackgroundColor();
        View viewDragged = (View) event.getLocalState();
        int ticketId = viewDragged.getId();
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
                // Send request to server to delete ticket.
                activity.deleteTicket(ticketId);
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
