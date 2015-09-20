package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * Created by Fletcher on 14/09/2015.
 */
public class NumberRollerPicker extends RollerPicker {

    private int mMin;
    private int mMax;
    private int mIncrement;
    private Paint mTextPaint;
    private OnNumberChangeListener mCallback;

    public NumberRollerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.NumberRollerPicker, 0, 0);
        try {
            mMin = a.getInteger(R.styleable.NumberRollerPicker_MinimumValue, 0);
            mMax = a.getInteger(R.styleable.NumberRollerPicker_MaximumValue, 100);
            mIncrement = a.getInteger(R.styleable.NumberRollerPicker_IncrementValue, 1);
        } finally {
            a.recycle();
        }
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        super.init(context, attrs);
    }

    public void setOnNumberChangeListener(OnNumberChangeListener callback) {
        mCallback = callback;
    }

    @Override
    protected int getCount() {
        int count = (mMax - mMin) / mIncrement;
        if (!isLoop()) {
            count++;
        }
        return count;
    }

    @Override
    protected Bitmap getBitmap(int position) {

        int value = mMin + (position * mIncrement);
        Bitmap b = Bitmap.createBitmap(getBitmapWidth(), getBitmapHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        mTextPaint.setTextSize(getBitmapHeight() / 1.2f);
        Rect bounds = new Rect();
        while (true) {
            mTextPaint.getTextBounds(String.valueOf(value), 0, String.valueOf(value).length(), bounds);
            if (bounds.width() > getBitmapWidth() / 1.2f) {
                mTextPaint.setTextSize(mTextPaint.getTextSize() - 10);
            } else {
                break;
            }
        }
        c.drawText(String.valueOf(value), getBitmapWidth() / 2, getBitmapHeight() / 2 + bounds.height() / 2, mTextPaint);

        return b;
    }

    @Override
    public void newPosition(float position) {
        invalidate();
    }

    @Override
    public void stopped(int position) {
        setGestureActive(false);
        invalidate();
        if (mCallback != null) {
            mCallback.numberSelected(
                    (int) ((mMin + getScrollPosition())
                            * mIncrement));
        }
    }

    @Override
    public void looped(int direction) {

        if (mCallback != null) {
            mCallback.looped(direction);
        }
    }

    public interface OnNumberChangeListener {
        void numberSelected(int number);

        void looped(int direction);
    }
}
