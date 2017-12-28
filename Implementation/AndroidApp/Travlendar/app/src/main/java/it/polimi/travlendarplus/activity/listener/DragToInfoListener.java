package it.polimi.travlendarplus.activity.listener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.retrofit.controller.event.DeleteEventController;
import it.polimi.travlendarplus.retrofit.controller.event.ScheduleEventController;

/**
 * Listens if a View is been dragged by the user to show info related to the event.
 */
public class DragToInfoListener implements View.OnDragListener {

    private CalendarActivity activity;

    public DragToInfoListener(CalendarActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onDrag(View viewReceiving, DragEvent event) {
        int color = viewReceiving.getDrawingCacheBackgroundColor();
        View viewDragged = (View) event.getLocalState();
        int eventId = viewDragged.getId();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                viewReceiving.setVisibility(View.VISIBLE);
                viewReceiving.setBackgroundColor(Color.parseColor("#BADFF3"));
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                viewReceiving.setBackgroundColor(Color.parseColor("#BADFF3"));
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                viewReceiving.setBackgroundColor(color);
                break;
            case DragEvent.ACTION_DROP:
                viewReceiving.setVisibility(View.GONE);
                // Show alert to see event's information.
                showAlert(eventId);
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

    /**
     * Shows an Alert containing the event's info.
     */
    private void showAlert(int eventId) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Event information")
                .setMessage(activity.getFocusedEvent().toString())
                .setPositiveButton("Schedule", (dialog, which) -> {
                    // Check if the event is already scheduled.
                    if (! activity.getFocusedEvent().isScheduled()) {
                        // Send schedule request to server.
                        activity.waitForServerResponse();
                        ScheduleEventController scheduleEventController =
                                new ScheduleEventController(activity.getScheduleEventHandler());
                        scheduleEventController.start(
                                activity.getToken(),
                                (int) activity.getFocusedEvent().getId()
                        );
                    } else {
                        Toast.makeText(activity, "The event is already scheduled!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    // Send delete event request to the server.
                    activity.waitForServerResponse();
                    DeleteEventController deleteEventController =
                            new DeleteEventController(activity.getDeleteEventHandler());
                    deleteEventController.start(activity.getToken(), eventId);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}