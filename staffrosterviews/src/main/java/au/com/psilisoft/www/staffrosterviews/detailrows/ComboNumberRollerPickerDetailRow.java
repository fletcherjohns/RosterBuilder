package au.com.psilisoft.www.staffrosterviews.detailrows;

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

import java.util.ArrayList;
import java.util.List;

import au.com.psilisoft.www.staffrosterviews.R;

/**
 * Created by Fletcher on 27/09/2015.
 */
public class ComboNumberRollerPickerDetailRow extends LinearLayout {

    private static final String SUPER_INSTANCE_STATE = "super_instance_state";
    private static final String STATE_LAYOUT_ID = "state_layout_id";
    private static final String STATE_VIEW_IDS = "state_view_ids";

    private List<NumberRollerPickerDetailRow> mDetailRows;
    private int[] mViewIds;
    private LinearLayout mLinearLayout;

    public ComboNumberRollerPickerDetailRow(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.ComboNumberRollerPickerDetailRowStyle);

        setOrientation(VERTICAL);
        mLinearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mLinearLayout.setLayoutParams(params);
        mLinearLayout.setPadding(
                getPaddingLeft(),
                0,
                getPaddingRight(),
                0
        );

        addView(mLinearLayout);
        TextView textView = new TextView(context, attrs);
        textView.setPadding(
                getPaddingLeft(),
                0,
                getPaddingRight(),
                0
        );
        setPadding(0, 0, 0, 0);
        textView.setId(NO_ID);
        addView(textView, 0);

        mDetailRows = new ArrayList<>();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View v;
        while ((v = getChildAt(2)) != null) {
            removeView(v);
            mLinearLayout.addView(v);
            if (v instanceof NumberRollerPickerDetailRow) {
                mDetailRows.add((NumberRollerPickerDetailRow) v);
            }
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

    public List<NumberRollerPickerDetailRow> getDetailRows() {
        return mDetailRows;
    }
}
