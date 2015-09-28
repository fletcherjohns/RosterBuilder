package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Fletcher on 27/09/2015.
 */
public class ComboNumberRollerPickerDetailRow extends LinearLayout {

    private static final String SUPER_INSTANCE_STATE = "super_instance_state";
    private static final String STATE_LAYOUT_ID = "state_layout_id";
    private static final String STATE_VIEW_IDS = "state_view_ids";

    private int[] mViewIds;
    private LinearLayout mLinearLayout;

    public ComboNumberRollerPickerDetailRow(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);
        mLinearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mLinearLayout.setLayoutParams(params);

        addView(mLinearLayout);
        TextView textView = new TextView(context, attrs);
        textView.setId(NO_ID);
        addView(textView, 0);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View v;
        while ((v = getChildAt(2)) != null) {
            removeView(v);
            mLinearLayout.addView(v);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState());
        if (mViewIds == null) {
            mLinearLayout.setId(View.generateViewId());
            mViewIds = new int[mLinearLayout.getChildCount()];
            for (int i = 0; i < mViewIds.length; i++) {
                mViewIds[i] = View.generateViewId();
                mLinearLayout.getChildAt(i).setId(mViewIds[i]);
            }
        }
        bundle.putInt(STATE_LAYOUT_ID, mLinearLayout.getId());
        bundle.putIntArray(STATE_VIEW_IDS, mViewIds);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        mLinearLayout.setId(bundle.getInt(STATE_LAYOUT_ID));
        mViewIds = bundle.getIntArray(STATE_VIEW_IDS);
        if (mViewIds != null) {
            for (int i = 0; i < mViewIds.length; i++) {
                mLinearLayout.getChildAt(i).setId(mViewIds[i]);
            }
        }
        state = bundle.getParcelable(SUPER_INSTANCE_STATE);
        super.onRestoreInstanceState(state);
    }
}
