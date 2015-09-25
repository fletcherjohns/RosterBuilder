package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Fletcher on 23/09/2015.
 */
public class NumberRollerPickerDetailRow extends DetailRow {

    private TextView mLabel;
    private NumberRollerPicker mPicker;

    public NumberRollerPickerDetailRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER_VERTICAL);

        mLabel = new TextView(context, attrs);
        mPicker = new NumberRollerPicker(context, attrs);
        addView(mLabel);
        addView(mPicker);
    }
}
