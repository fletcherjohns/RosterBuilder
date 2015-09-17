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

import java.util.HashMap;

/**
 * Created by Fletcher on 14/09/2015.
 */
public class CustomNumberPicker extends View {

    private static final String TAG = "tag";
    private int mMin;
    private int mIncrement;
    private int mNumberOfSides = 12;

    private int mWidth;
    private int mHeight;

    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mApothem;

    private Paint mTextPaint;
    private Paint mBitmapPaint;
    private Camera mCamera;
    private Matrix mMatrix;
    private HashMap<Integer, Bitmap> mBitmaps;

    private GestureDetector mGestureDetector;
    private boolean mGestureActive = false;
    private ScrollManager mScrollManager;


    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CustomNumberPicker, 0, 0);
        final int max;
        try {
            mMin = a.getInteger(R.styleable.CustomNumberPicker_MinimumValue, 0);
            max = a.getInteger(R.styleable.CustomNumberPicker_MaximumValue, 100);
            mIncrement = a.getInteger(R.styleable.CustomNumberPicker_IncrementValue, 1);

        } finally {
            a.recycle();
        }
        mGestureDetector = new GestureDetector(context, new GestureDetectorListener());
        mScrollManager = new ScrollManager((max - mMin) / mIncrement);
        mScrollManager.setCallback(new ScrollManager.ScrollCallback() {
            @Override
            public void newPosition() {
                postInvalidate();
            }

            @Override
            public void stopped() {
                mGestureActive = false;
            }
        });
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
        mBitmaps = new HashMap<>(mNumberOfSides / 2 + 1);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        //TODO we need to work out how to fit the numbers in according to the actual values shown
        //TODO and textSize etc.

        float radius = (float) (mHeight);
        while (true) {
            setMatrix(Math.round(mScrollManager.getPosition()) - 1);
            RectF rectF = new RectF(0, 0, mBitmapWidth, mBitmapHeight);
            mMatrix.mapRect(rectF);
            if (rectF.top < 0) {
                radius -= 1;
            } else {
                break;
            }
        }

        mApothem = (float) (radius * Math.cos(Math.PI / mNumberOfSides));

        mBitmapHeight = (int) (radius * 2 * Math.sin(Math.PI / mNumberOfSides)) + 1;
        mBitmapWidth = (int) (mWidth * .8);

        mTextPaint.setTextSize(mBitmapWidth / 1.2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centrePosition = Math.round(mScrollManager.getPosition());

        if (mGestureActive) {
            for (int i = -(mNumberOfSides / 4); i < 0; i++) {
                drawBitmap(canvas, centrePosition + i);
                drawBitmap(canvas, centrePosition - i);
            }
        }
        drawBitmap(canvas, centrePosition);

    }

    private void drawBitmap(Canvas canvas, int position) {

        position %= mScrollManager.getCount();
        if (position < 0) {
            position += mScrollManager.getCount();
        }
        int value = mMin + position * mIncrement;

        Bitmap b = mBitmaps.get(value);
        if (b == null) {
            b = getBitmap(value);
            mBitmaps.put(value, b);
        }
        if (setMatrix(position)) {
            canvas.drawBitmap(b, mMatrix, mBitmapPaint);
        }

    }

    private Bitmap getBitmap(int value) {

        Bitmap b = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        float textHeight = mTextPaint.getTextSize();
        c.drawText(String.valueOf(value), mBitmapWidth / 2, (int) (mBitmapHeight / 2. + textHeight / 2.), mTextPaint);
        return b;
    }

    private boolean setMatrix(int position) {

        RectF rect = getChildDimensions();

        float rotation = (mScrollManager.getPosition() - position) * 360 / mNumberOfSides;
        mCamera.save();

        mCamera.translate(0, 0, mApothem);
        mCamera.rotateX(rotation);
        mCamera.translate(0, 0, -mApothem);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(-mBitmapWidth / 2, -mBitmapHeight / 2);
        mMatrix.postTranslate(mBitmapWidth / 2 + rect.left, mBitmapHeight / 2 + rect.top);

        float[] points = {
                0, 0,
                0, 100,
        };
        mMatrix.mapPoints(points);
        return points[1] < points[3];
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

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mScrollManager.start();
                mGestureActive = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mScrollManager.stop();
                break;
        }
        return true;
    }

    private class GestureDetectorListener extends android.view.GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mScrollManager.scroll(distanceY / mBitmapHeight);
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScrollManager.fling(-velocityY / 30000);
            return true;
        }

    }
}
