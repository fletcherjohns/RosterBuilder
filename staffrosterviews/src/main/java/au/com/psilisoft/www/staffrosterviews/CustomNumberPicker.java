package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Created by Fletcher on 14/09/2015.
 */
public class CustomNumberPicker extends View {

    private int mMin;
    private int mMax;
    private int mIncrement;
    private double mScrollPosition = 0;

    private int mWidth;
    private int mHeight;
    private int mBitmapEdge;

    private Paint mPaint;
    private Camera mCamera;
    private Matrix mMatrix;


    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CustomNumberPicker, 0, 0);
        try {
            mMin = a.getInteger(R.styleable.CustomNumberPicker_MinimumValue, 0);
            mMax = a.getInteger(R.styleable.CustomNumberPicker_MaximumValue, 100);
            mIncrement = a.getInteger(R.styleable.CustomNumberPicker_IncrementValue, 1);

        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(12f);
        mCamera = new Camera();
        mMatrix = new Matrix();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mWidth = r - l;
        mHeight = b - t;
        mBitmapEdge = (int) (mHeight / Math.sqrt(4 + 2 * Math.sqrt(2)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int firstPosition = (int) ((mScrollPosition - 2) * mIncrement);
        int lastPosition = (int) ((mScrollPosition + 2) * mIncrement);
        float distanceFromCentre;
        String number;

        for (int i = firstPosition; i <= lastPosition; i += mIncrement) {
            if (i >= 0) {
                number = String.valueOf(mMin + (i * mIncrement));
                distanceFromCentre = Math.round(mScrollPosition - i);

                Bitmap b = getBitmap(i);

            }
        }
    }


    private Bitmap getBitmap(int position) {

        Bitmap b = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawText(String.valueOf(mMin + (position * mIncrement)), mBitmapEdge, mBitmapEdge, mPaint);
        return b;
    }
}
