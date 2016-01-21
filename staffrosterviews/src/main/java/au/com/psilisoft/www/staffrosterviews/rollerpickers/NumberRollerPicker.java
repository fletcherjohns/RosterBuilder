package au.com.psilisoft.www.staffrosterviews.rollerpickers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import au.com.psilisoft.www.staffrosterviews.R;

/**
 * Created by Fletcher on 14/09/2015.
 */
public class NumberRollerPicker extends RollerPicker {

    private int mMin;
    private int mMax;
    private int mIncrement;
    private String mFormatString;
    private NumberFormat mFormat;
    private Paint mTextPaint;
    private OnNumberChangeListener mCallback;


    public  NumberRollerPicker(Context context) {
        this(context, null);
    }
    public NumberRollerPicker(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.NumberRollerPickerStyle);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyleAttrs) {

        mTextPaint = new Paint();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.NumberRollerPicker, 0, 0);
        try {
            mMin = a.getInteger(R.styleable.NumberRollerPicker_MinimumValue, 0);
            mMax = a.getInteger(R.styleable.NumberRollerPicker_MaximumValue, 100);
            mIncrement = a.getInteger(R.styleable.NumberRollerPicker_IncrementValue, 1);
            mFormatString = a.getString(R.styleable.NumberRollerPicker_NumberFormat);
            mTextPaint.setColor(a
                    .getColor(R.styleable.NumberRollerPicker_android_textColor,
                            getResources().getColor(R.color.dark_text_primary)));
            mTextPaint.setTextSize(a.getDimensionPixelSize(R.styleable.NumberRollerPicker_android_textSize, 30));
        } finally {
            a.recycle();
        }
        mTextPaint.setAntiAlias(true);

        if (mFormatString != null) {
            mFormat = new DecimalFormat(mFormatString);
        } else {
            mFormat = new DecimalFormat();
        }
        super.init(context, attrs, defStyleAttrs);
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        mMin = min;
        setCount((mMax - mMin) / mIncrement);
        invalidate();
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
        setCount((mMax - mMin) / mIncrement);
        invalidate();
    }


    public int getIncrement() {
        return mIncrement;
    }

    public String getFormatString() {
        return mFormatString;
    }

    public void setFormatString(String formatString) {
        mFormatString = formatString;
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
    protected Rect getBitmapSize() {

        Rect rect = new Rect();

        String s = mFormat.format(mMax);
        mTextPaint.getTextBounds(s, 0, s.length(), rect);
        int w = (int) (rect.width() * 1.4f);
        int h = (int) (rect.height() * 1.4f);
        return new Rect(0, 0, w, h);
    }

    @Override
    protected Bitmap getBitmap(int position) {

        int value = mMin + (position * mIncrement);
        Bitmap b = Bitmap.createBitmap(getBitmapWidth(), getBitmapHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        String s = mFormat.format(value);

        Rect bounds = new Rect();
        float textWidth = mTextPaint.measureText(s);
        mTextPaint.getTextBounds(s, 0, s.length(), bounds);
        c.drawText(s, getBitmapWidth() / 2f - textWidth / 2, getBitmapHeight() / 2f + bounds.height() / 2f, mTextPaint);

        return b;
    }


    public float getTextSize() {
        return mTextPaint.getTextSize();
    }

    public void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
        requestLayout();
    }

    @Override
    public void newPosition(double position) {
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

    public int getValue() {
        return (int) (mMin + getScrollPosition() * mIncrement);
    }

    public void setValue(Integer value) {
        setScrollPosition(mMin + value / mIncrement);
    }

    @Override
    public void looped(int direction) {

        if (mCallback != null) {
            mCallback.looped(direction);
        }
    }

    public void setIncrement(int increment) {
        mIncrement = increment;
        setCount((mMax - mMin) / mIncrement);
        invalidate();
    }

    public interface OnNumberChangeListener {
        void numberSelected(int number);

        void looped(int direction);
    }
}
