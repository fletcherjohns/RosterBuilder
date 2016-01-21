package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import au.com.psilisoft.www.staffrosterviews.StaffDetailsView;

/**
 * Created by Fletcher on 5/10/2015.
 */
public class StaffDynamicActivity extends Activity {

    public static final String EXTRA_STAFF_ID = "staff_detail_view_extra_staff_id";

    private static final String STATE_VIEW_IDS = "state_view_ids";

    private FrameLayout mFrameLayout;
    private StaffDetailsView mStaffDetailView;
    private int[] mViewIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long staffId = getIntent().getLongExtra(EXTRA_STAFF_ID, -1);

        mFrameLayout = new FrameLayout(this);
        setContentView(mFrameLayout);

        mStaffDetailView = new StaffDetailsView(this, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mStaffDetailView.setLayoutParams(params);
        mFrameLayout.addView(mStaffDetailView);

        if (savedInstanceState != null) {
            mViewIds = savedInstanceState.getIntArray(STATE_VIEW_IDS);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mViewIds == null) {
            int count = mFrameLayout.getChildCount();
            mViewIds = new int[count];
            for (int i = 0; i < count; i++) {
                mViewIds[i] = mFrameLayout.getChildAt(i).getId();
            }
        }
        outState.putIntArray(STATE_VIEW_IDS, mViewIds);
    }

}
