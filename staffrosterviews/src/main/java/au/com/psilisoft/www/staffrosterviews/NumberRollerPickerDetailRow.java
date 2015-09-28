package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Fletcher on 23/09/2015.
 */
public class NumberRollerPickerDetailRow extends DetailRow<Integer> {

    private NumberRollerPicker mPicker;

    public NumberRollerPickerDetailRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getValueWidget(Context context, AttributeSet attrs, int defStyleAttrs) {
        if (mPicker == null) {
            mPicker = new NumberRollerPicker(context, attrs);
            mPicker.setOnNumberChangeListener(new NumberRollerPicker.OnNumberChangeListener() {
                @Override
                public void numberSelected(int number) {
                    onValueChanged();
                }

                @Override
                public void looped(int direction) {
                }
            });
        }
        return mPicker;
    }

    @Override
    public Integer getValue() {
        return mPicker.getValue();
    }

    @Override
    public void setValue(Integer value) {
        mPicker.setValue(value);
    }

    @Override
    protected void putValueInTheBundle(Integer value, Bundle bundle, String tag) {
        bundle.putInt(tag, value);

    }

    @Override
    protected Integer getValueBackOutOfTheBundle(Bundle bundle, String tag) {
        return bundle.getInt(tag);
    }
}
