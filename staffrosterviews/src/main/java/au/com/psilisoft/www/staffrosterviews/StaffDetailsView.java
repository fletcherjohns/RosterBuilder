package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Fletcher on 14/09/2015.
 */
public class StaffDetailsView extends LinearLayout {

    public StaffDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StaffDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_staff_details, this);
        setOrientation(VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

}
