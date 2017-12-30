package it.polimi.travlendarplus.activity.listener;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;

/**
 * Adds on touch listener to a view, allowing it to be dragged around or to be double clicked.
 */
public class MyTouchEventListener implements View.OnTouchListener {

    private CalendarActivity activity;
    private GenericEvent genericEvent;

    public MyTouchEventListener ( CalendarActivity activity, GenericEvent genericEvent ) {
        this.activity = activity;
        this.genericEvent = genericEvent;
    }

    // Allows the view to be dragged around.
    public boolean onTouch ( View viewDragged, MotionEvent motionEvent ) {
        switch ( motionEvent.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                ClipData data = ClipData.newPlainText( "", "" );
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder( viewDragged );
                viewDragged.startDrag( data, shadowBuilder, viewDragged, 0 );
                viewDragged.setVisibility( View.INVISIBLE );
                activity.setFocusedEvent( genericEvent );
                activity.setInfoDeleteTVVisibility( View.VISIBLE );
                return true;
            case MotionEvent.ACTION_UP:
                viewDragged.performClick();
                return false;
            default:
                return false;
        }
    }
}
