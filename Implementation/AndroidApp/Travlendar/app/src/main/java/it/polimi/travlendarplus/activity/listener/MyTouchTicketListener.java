package it.polimi.travlendarplus.activity.listener;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;


/**
 * Adds on touch listener to a view, allowing it to be dragged around.
 */
public final class MyTouchListener implements View.OnTouchListener {

    public boolean onTouch(View viewDragged, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(viewDragged);
                viewDragged.startDrag(data, shadowBuilder, viewDragged, 0);
                viewDragged.setVisibility(View.INVISIBLE);
                return true;
            case MotionEvent.ACTION_UP:
                viewDragged.performClick();
                return false;
            default:
                return false;
        }
    }
}
