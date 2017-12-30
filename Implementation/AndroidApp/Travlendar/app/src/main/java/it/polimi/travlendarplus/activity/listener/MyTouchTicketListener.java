package it.polimi.travlendarplus.activity.listener;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;


/**
 * Adds on touch listener to a view, allowing it to be dragged around.
 */
public final class MyTouchTicketListener implements View.OnTouchListener {
    // Allows the view to be dragged around.
    public boolean onTouch ( View viewDragged, MotionEvent motionEvent ) {
        switch ( motionEvent.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                ClipData data = ClipData.newPlainText( "", "" );
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder( viewDragged );
                viewDragged.startDrag( data, shadowBuilder, viewDragged, 0 );
                viewDragged.setVisibility( View.INVISIBLE );
                return true;
            case MotionEvent.ACTION_UP:
                viewDragged.performClick();
                return false;
            default:
                return false;
        }
    }
}
