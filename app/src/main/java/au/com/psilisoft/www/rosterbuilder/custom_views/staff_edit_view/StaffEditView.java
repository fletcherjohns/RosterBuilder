package au.com.psilisoft.www.rosterbuilder.custom_views.staff_edit_view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import au.com.psilisoft.www.rosterbuilder.R;


/**
 * Created by Fletcher on 9/09/2015.
 */
public class StaffEditView extends ViewGroup {



    public StaffEditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.view_staff_edit, this);
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }
}
