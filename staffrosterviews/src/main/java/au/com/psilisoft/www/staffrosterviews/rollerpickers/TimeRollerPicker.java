package au.com.psilisoft.www.staffrosterviews.rollerpickers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import au.com.psilisoft.www.staffrosterviews.R;
import au.com.psilisoft.www.staffrosterviews.rollerpickers.NumberRollerPicker;

/**
 * Created by Fletcher on 22/09/2015.
 */
public class TimeRollerPicker extends LinearLayout {

    private int mMinuteIncrement;
    private int mHourIncrement;

    private NumberRollerPicker mHourRollerPicker;
    private NumberRollerPicker mMinuteRollerPicker;
    private Paint mPaint;

    public TimeRollerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeRollerPicker);
        try {
            mMinuteIncrement = a.getInt(R.styleable.TimeRollerPicker_MinuteIncrementValue, 1);
            mHourIncrement = a.getInt(R.styleable.TimeRollerPicker_HourIncrementValue, 1);
        } finally {
            a.recycle();
        }
        View.inflate(context, R.layout.view_time_roller_picker, this);
        mHourRollerPicker = (NumberRollerPicker) findViewById(R.id.hour_roller_picker);
        mHourRollerPicker.setIncrement(mHourIncrement);

        mMinuteRollerPicker = (NumberRollerPicker) findViewById(R.id.minute_roller_picker);
        mMinuteRollerPicker.setIncrement(mMinuteIncrement);
        mMinuteRollerPicker.setOnNumberChangeListener(new NumberRollerPicker.OnNumberChangeListener() {
            @Override
            public void numberSelected(int number) {

            }

            @Override
            public void looped(int direction) {
                mHourRollerPicker.setScrollPosition(mHourRollerPicker.getScrollPosition() + direction);
            }
        });

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Log.v("tag", "onDraw()");
        mPaint.setTextSize(mHourRollerPicker.getTextSize());
        Rect rect = new Rect();
        mPaint.getTextBounds(":", 0, 1, rect);

        canvas.drawText(":", getWidth() / 2, getHeight() / 2 + rect.height() / 2, mPaint);
    }
}
