package it.polimi.travlendarplus.activity.listener;


import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Listens if a View is been dragged by the user to schedule the event related.
 */
public final class DragToScheduleListener implements View.OnDragListener {

    @Override
    public boolean onDrag(View viewReceiving, DragEvent event) {
        int action = event.getAction();
        int color = viewReceiving.getDrawingCacheBackgroundColor();
        View viewDragged = (View) event.getLocalState();
        int eventId = viewDragged.getId();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                viewReceiving.setBackgroundColor(Color.parseColor("#00FF00"));
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                viewReceiving.setBackgroundColor(color);
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                ViewGroup owner = (ViewGroup) viewDragged.getParent();
                owner.removeView(viewDragged);
                RelativeLayout container = (RelativeLayout) viewReceiving;
                container.addView(viewDragged, 0);
                viewDragged.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                viewReceiving.setBackgroundColor(color);
                viewDragged.setVisibility(View.VISIBLE);
            default:
                break;
        }
        return true;
    }
}
