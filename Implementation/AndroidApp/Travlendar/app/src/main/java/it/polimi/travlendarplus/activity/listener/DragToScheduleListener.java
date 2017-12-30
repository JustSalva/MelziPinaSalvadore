package it.polimi.travlendarplus.activity.listener;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.retrofit.controller.event.ScheduleEventController;

/**
 * Listens if a View is been dragged by the user to schedule the event related.
 */
public final class DragToScheduleListener implements View.OnDragListener {

    private Context context;
    private CalendarActivity calendarActivity;

    public DragToScheduleListener ( Context context, CalendarActivity calendarActivity ) {
        this.context = context;
        this.calendarActivity = calendarActivity;
    }

    @Override
    public boolean onDrag ( View viewReceiving, DragEvent event ) {
        View viewDragged = ( View ) event.getLocalState();
        int eventId = viewDragged.getId();
        // Get if event is scheduled from the View background color.
        int color = viewReceiving.getDrawingCacheBackgroundColor();
        Drawable background = viewDragged.getBackground();
        int bgColor = ( ( ColorDrawable ) background ).getColor();
        boolean scheduled = ( bgColor == Color.parseColor( "#FF88CF92" ) ) ||
                ( bgColor == Color.parseColor( "#FFFF00" ) );

        switch ( event.getAction() ) {
            case DragEvent.ACTION_DRAG_STARTED:
                viewReceiving.setBackgroundColor( Color.parseColor( "#00FF00" ) );
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                viewReceiving.setBackgroundColor( Color.parseColor( "#00FF00" ) );
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                viewReceiving.setBackgroundColor( color );
                break;
            case DragEvent.ACTION_DROP:
                // Send request to schedule event.
                if ( !scheduled ) {
                    // Send request to server.
                    calendarActivity.waitForServerResponse();
                    ScheduleEventController scheduleEventController =
                            new ScheduleEventController( calendarActivity.getScheduleEventHandler() );
                    scheduleEventController.start( calendarActivity.getToken(), eventId );
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                viewReceiving.setBackgroundColor( color );
                viewDragged.setVisibility( View.VISIBLE );
            default:
                break;
        }
        return true;
    }
}
