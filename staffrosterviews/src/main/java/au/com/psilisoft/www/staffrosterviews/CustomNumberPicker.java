package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * Created by Fletcher on 14/09/2015.
 */
public class CustomNumberPicker extends View {

    private int mMin;
    private int mMax;
    private int mIncrement;
    private int mCount;
    private double mScrollPosition = 0;

    private int mWidth;
    private int mHeight;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mRadius;
    private float mApothem;

    private Paint mTextPaint;
    private Paint mBitmapPaint;
    private Camera mCamera;
    private Matrix mMatrix;

    private GestureDetector mGestureDetector;


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
        mCount = (mMax - mMin) / mIncrement;
        mGestureDetector = new GestureDetector(context, new GestureDetectorListener());
        init();
    }

    private void init() {

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mBitmapPaint = new Paint();
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setAntiAlias(true);
        mCamera = new Camera();
        mMatrix = new Matrix();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mWidth = r - l;
        mHeight = b - t;
        mBitmapHeight = (int) (mHeight / Math.sqrt(4 + 2 * Math.sqrt(2)) * 1.8);
        mBitmapWidth = (int) (mWidth * .8);
        mRadius = (float) (mBitmapHeight / (2. * Math.sin(Math.PI / 8.)));
        mApothem = (float) (mRadius * Math.cos(Math.PI / 8.));

        Log.v("password", "radius = " + mRadius + ", apothem = " + mApothem);
        mTextPaint.setTextSize(mBitmapHeight / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centrePosition = (int) Math.round(mScrollPosition);

        for (int i = -2; i <=0; i++) {
            drawBitmap(canvas, centrePosition + i);
            if (i != 0) {
                drawBitmap(canvas, centrePosition - i);
            }
        }
    }

    private void drawBitmap(Canvas canvas, int position) {

        int value = position % mCount;
        if (value < 0) {
            value += mCount;
        }
        value *= mIncrement;

        Bitmap b = getBitmap(value);
        setMatrix(position);
        RectF rect = new RectF(0, 0, mBitmapWidth, mBitmapHeight);
        mMatrix.mapRect(rect);
        Log.v("password", "left: " + rect.left + ", top: " + rect.top + ", right: " + rect.right + ", bottom: " + rect.bottom);
        if (rect.height() > 0) {
            canvas.drawBitmap(b, mMatrix, mBitmapPaint);
        }
    }



    private Bitmap getBitmap(int value) {

        Bitmap b = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawColor(Color.WHITE);
        float textHeight = mTextPaint.getTextSize();
        c.drawText(String.valueOf(value), mBitmapWidth / 2, mBitmapHeight / 2 + textHeight / 2, mTextPaint);
        return b;
    }

    private void setMatrix(int position) {

        RectF rect = getChildDimensions();

        float rotation = (float) (mScrollPosition - position) * 45;
        mCamera.save();
        mCamera.translate(0, 0, mApothem);
        mCamera.rotateX(rotation);
        mCamera.translate(0, 0, -mApothem);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(-mBitmapWidth / 2, -mBitmapHeight / 2);
        mMatrix.postTranslate(mBitmapWidth / 2 + rect.left, mBitmapHeight / 2 + rect.top);
    }

    private RectF getChildDimensions() {

        int halfFrameWidth = mWidth / 2;
        int halfFrameHeight = mHeight / 2;
        int halfChildWidth = mBitmapWidth / 2;
        int halfChildHeight = mBitmapHeight / 2;

        // Centre the view horizontally and vertically
        int left = halfFrameWidth - halfChildWidth;
        int top = halfFrameHeight - halfChildHeight;

        return new RectF(left, top, left + mBitmapWidth, top + mBitmapHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private class GestureDetectorListener extends android.view.GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mScrollPosition += distanceY / mBitmapHeight;
            mScrollPosition %= mCount;
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
