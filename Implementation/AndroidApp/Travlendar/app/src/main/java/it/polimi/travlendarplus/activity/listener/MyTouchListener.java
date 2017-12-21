package it.polimi.travlendarplus.activity.listener;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;


public final class MyTouchListener implements View.OnTouchListener {
    public boolean onTouch(View viewDragged, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(viewDragged);
            viewDragged.startDrag(data, shadowBuilder, viewDragged, 0);
            viewDragged.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }
}
