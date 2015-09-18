package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

/**
 * Created by Fletcher on 14/09/2015.
 */
public class CustomNumberPicker extends View {

    private static final String TAG = "tag";
    private static final int UP_ARROW = -1;
    private static final int DOWN_ARROW = -2;
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
    private Paint mArrowPaint;

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
        mScrollManager = new ScrollManager((max - mMin) / mIncrement, true);
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

        mArrowPaint = new Paint();
        mArrowPaint.setColor(Color.BLACK);
        mArrowPaint.setStyle(Paint.Style.FILL);
        mArrowPaint.setAntiAlias(true);

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

        float[] points = {
                0f, 0f,
                0f, (float) h
        };
        Matrix m = new Matrix();
        Camera c = new Camera();
        c.save();
        c.translate(0, 0, -(h / 2));
        c.getMatrix(m);
        c.restore();
        m.preTranslate(0, -(h / 2));
        m.postTranslate(0, h / 2);
        Log.v(TAG, String.valueOf(points[3] - points[1]));
        m.mapPoints(points);


        float radius = Math.abs(points[3] - points[1]) / 2;
        Log.v(TAG, String.valueOf(points[3] - points[1]));

        mApothem = (float) (radius * Math.cos(Math.PI / mNumberOfSides));

        mBitmapHeight = (int) (radius * 2 * Math.sin(Math.PI / mNumberOfSides)) + 1;
        mBitmapWidth = (int) (mWidth * .8);

        mTextPaint.setTextSize(Math.min(mBitmapHeight / 1.2f, mBitmapWidth / 1.2f));
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
        } else {
            drawUpArrow(canvas);
            drawDownArrow(canvas);
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

    public void drawUpArrow(Canvas canvas) {
        setMatrix(Math.round(mScrollManager.getPosition()) - 1);
        canvas.drawBitmap(getBitmap(UP_ARROW), mMatrix, mArrowPaint);
    }

    public void drawDownArrow(Canvas canvas) {
        setMatrix(Math.round(mScrollManager.getPosition()) + 1);
        canvas.drawBitmap(getBitmap(DOWN_ARROW), mMatrix, mArrowPaint);
    }

    private Bitmap getBitmap(int value) {

        Bitmap b = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        switch (value) {
            case UP_ARROW:

                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(mBitmapWidth * 2 / 5, mBitmapHeight);
                path.lineTo(mBitmapWidth / 2, mBitmapHeight * 2 / 3);
                path.lineTo(mBitmapWidth * 3 / 5, mBitmapHeight);
                path.lineTo(mBitmapWidth * 2 / 5, mBitmapHeight);
                path.close();

                c.drawPath(path, mArrowPaint);
                break;
            case DOWN_ARROW:

                path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(mBitmapWidth * 2 / 5, 0);
                path.lineTo(mBitmapWidth / 2, mBitmapHeight / 3);
                path.lineTo(mBitmapWidth * 3 / 5, 0);
                path.lineTo(mBitmapWidth * 2 / 5, 0);
                path.close();

                c.drawPath(path, mArrowPaint);
                break;
            default:
                float textHeight = mTextPaint.getTextSize();
                c.drawText(String.valueOf(value), mBitmapWidth / 2, (int) (mBitmapHeight / 2. + textHeight / 2.), mTextPaint);
        }
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
                mScrollManager.interrupt();
                mGestureActive = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mScrollManager.ensureThreadIsAlive();
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
