package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Fletcher on 27/09/2015.
 */
public class StateSaveLinearLayout extends LinearLayout {

    private static final String SUPER_INSTANCE_STATE = "super_instance_state";
    private static final String STATE_VIEW_IDS = "state_view_ids";

    int[] mViewIds;


    public StateSaveLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState());
        if (mViewIds == null) {
            mViewIds = new int[getChildCount()];
            for (int i = 0; i < mViewIds.length; i++) {
                mViewIds[i] = View.generateViewId();
                getChildAt(i).setId(mViewIds[i]);
            }
        }
        bundle.putIntArray(STATE_VIEW_IDS, mViewIds);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        state = bundle.getParcelable(SUPER_INSTANCE_STATE);
        mViewIds = bundle.getIntArray(STATE_VIEW_IDS);
        if (mViewIds != null) {
            for (int i = 0; i < mViewIds.length; i++) {
                getChildAt(i).setId(mViewIds[i]);
            }
        }
        super.onRestoreInstanceState(state);
    }
}
