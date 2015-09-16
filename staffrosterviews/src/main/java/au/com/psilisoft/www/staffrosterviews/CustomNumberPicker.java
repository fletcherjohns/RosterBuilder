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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Fletcher on 14/09/2015.
 */
public class CustomNumberPicker extends View {

    private static final float FLING_VELOCITY_THRESHOLD = 0.01f;
    private int mMin;
    private int mMax;
    private int mIncrement;
    private int mNumberOfSides = 16;

    private int mCount;
    private double mScrollPosition = 0;

    private int mWidth;
    private int mHeight;

    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mApothem;

    private Paint mTextPaint;
    private Paint mBitmapPaint;
    private Camera mCamera;
    private Matrix mMatrix;

    private GestureDetector mGestureDetector;
    private float mScrollVelocity;
    private SnapThread mSnapThread;
    private FlingThread mFlingThread;


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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        float radius = (float) (mHeight);
        mApothem = (float) (radius * Math.cos(Math.PI / mNumberOfSides));

        mBitmapHeight = (int) (radius * 2 * Math.sin(Math.PI / mNumberOfSides));
        mBitmapWidth = (int) (mWidth * .8);

        mTextPaint.setTextSize(mBitmapHeight / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centrePosition = (int) Math.round(mScrollPosition);

        if (mScrollVelocity != 0) {
            for (int i = -(mNumberOfSides / 4); i <= 0; i++) {

                drawBitmap(canvas, centrePosition + i);
                if (i != 0) {
                    drawBitmap(canvas, centrePosition - i);
                }
            }
        } else {
            drawBitmap(canvas, centrePosition);
        }
    }

    private void drawBitmap(Canvas canvas, int position) {

        int value = position % mCount;
        if (value < 0) {
            value += mCount;
        }
        value = mMin + value * mIncrement;

        Bitmap b = getBitmap(value);
        if (setMatrix(position)) {
            canvas.drawBitmap(b, mMatrix, mBitmapPaint);
        }

    }


    private Bitmap getBitmap(int value) {

        Bitmap b = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        float textHeight = mTextPaint.getTextSize();
        c.drawText(String.valueOf(value), mBitmapWidth / 2, mBitmapHeight / 2 + textHeight / 2, mTextPaint);
        return b;
    }

    private boolean setMatrix(int position) {

        RectF rect = getChildDimensions();

        float rotation = (float) ((mScrollPosition - position) * 360 / mNumberOfSides);
        if (Math.abs(rotation) > 40) {
            return false;
        }
        mCamera.save();

        mCamera.translate(0, 0, mApothem);
        mCamera.rotateX(rotation);
        mCamera.translate(0, 0, -mApothem);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(-mBitmapWidth / 2, -mBitmapHeight / 2);
        mMatrix.postTranslate(mBitmapWidth / 2 + rect.left, mBitmapHeight / 2 + rect.top);

        return true;
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
        if (!mGestureDetector.onTouchEvent(event)) {
            mSnapThread = new SnapThread((int) Math.round(mScrollPosition));
            mSnapThread.start();
            return false;
        } else {
            return true;
        }
    }

    private class GestureDetectorListener extends android.view.GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            try {
                mFlingThread.interrupt();
            } catch (Exception ex) {
                // Thread probably null
            }
            try {
                mSnapThread.interrupt();
            } catch (Exception ex) {
                // Thread probably null
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mScrollPosition += distanceY / mBitmapHeight;
            mScrollPosition %= mCount;
            mScrollVelocity = 1;
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScrollVelocity = -velocityY / 80000;
            mFlingThread = new FlingThread();
            mFlingThread.start();
            return true;
        }

    }

    private class FlingThread extends Thread {

        @Override
        public void run() {

            while (Math.abs(mScrollVelocity) > FLING_VELOCITY_THRESHOLD) {
                mScrollVelocity *= 0.95f;

                mScrollPosition += mScrollVelocity;
                mScrollPosition %= mCount;
                post(new RequestLayoutRunnable());
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }
            mSnapThread = new SnapThread((int) Math.round(mScrollPosition));
            mSnapThread.start();
        }
    }

    private class SnapThread extends Thread {

        int mSnapPosition;

        public SnapThread(int snapPosition) {
            mSnapPosition = snapPosition;
        }

        @Override
        public void run() {

            while (Math.abs((mSnapPosition - mScrollPosition)) > 0.001) {
                mScrollVelocity = (float) ((mSnapPosition - mScrollPosition) / 10);
                mScrollPosition += mScrollVelocity;
                post(new RequestLayoutRunnable());
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }
            mScrollVelocity = 0;
            mScrollPosition = mSnapPosition;
            post(new RequestLayoutRunnable());
        }
    }

    private class RequestLayoutRunnable implements Runnable {

        @Override
        public void run() {
            invalidate();
        }
    }
}
