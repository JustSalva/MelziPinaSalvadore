package it.polimi.travlendarplus.activity.listener;


import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Listens if a View if been dragged by the user.
 */
public final class MyDragListener implements View.OnDragListener {

    @Override
    public boolean onDrag(View viewReceiving, DragEvent event) {
        int action = event.getAction();
        View viewDragged = (View) event.getLocalState();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                viewReceiving.setBackgroundColor(Color.parseColor("#000000"));
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                viewReceiving.setBackgroundColor(Color.parseColor("#00FF00"));
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                viewReceiving.setBackgroundColor(Color.parseColor("#0000FF"));
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                ViewGroup owner = (ViewGroup) viewDragged.getParent();
                owner.removeView(viewDragged);
                LinearLayout container = (LinearLayout) viewReceiving;
                container.addView(viewDragged, 0);
                viewDragged.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                viewDragged.setVisibility(View.VISIBLE);
            default:
                break;
        }
        return true;
    }
}
