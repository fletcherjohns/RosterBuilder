package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.psilisoft.www.staffrosterviews.scrollmanager.ScrollCallback;
import au.com.psilisoft.www.staffrosterviews.scrollmanager.ScrollManager;

/**
 * Created by Fletcher on 20/09/2015.
 */
public abstract class RollerPicker extends View implements ScrollCallback {

    private static final int SIDES_PER_POSITION = 1;
    private static final int SIZE_SMALL = 1;
    private static final int SIZE_MEDIUM = 2;
    private static final int SIZE_LARGE = 3;
    private static final String TAG = "tag";
    private static final int UP_ARROW = -1;
    private static final int DOWN_ARROW = -2;
    private static final String SUPER_INSTANCE_STATE = "super_instance_state";
    private static final String STATE_POSITION = "state_position";

    private boolean mLoop;
    private int mSize = SIZE_MEDIUM;
    private int mNumberOfSides;

    private int mWidth;
    private int mHeight;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mApothem;

    private Camera mCamera;
    private Matrix mMatrix;
    private HashMap<Integer, List<Bitmap>> mBitmaps;
    private Paint mBitmapPaint;
    private Paint mArrowPaint;

    private GestureDetector mGestureDetector;
    private boolean mGestureActive = false;
    private ScrollManager mScrollManager;

    public RollerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle savedInstanceState = new Bundle();
        savedInstanceState.putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState());
        savedInstanceState.putFloat(STATE_POSITION, mScrollManager.getPosition());
        return savedInstanceState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle savedInstanceState = (Bundle) state;
        state = savedInstanceState.getParcelable(SUPER_INSTANCE_STATE);
        mScrollManager.setPosition(savedInstanceState.getFloat(STATE_POSITION));
        super.onRestoreInstanceState(state);
    }

    protected abstract int getCount();

    public boolean isLoop() {
        return mLoop;
    }

    public boolean isGestureActive() {
        return mGestureActive;
    }

    public void setGestureActive(boolean gestureActive) {
        mGestureActive = gestureActive;
    }

    protected void init(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.RollerPicker, 0, 0);
        try {
            mLoop = a.getBoolean(R.styleable.RollerPicker_Loop, false);
            mSize = a.getInt(R.styleable.RollerPicker_Size, 1);
        } finally {
            a.recycle();
        }

        mGestureDetector = new GestureDetector(context, new GestureDetectorListener());

        mCamera = new Camera();
        mMatrix = new Matrix();
        mBitmaps = new HashMap<>();

        mBitmapPaint = new Paint();
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setAntiAlias(true);

        mArrowPaint = new Paint();
        mArrowPaint.setColor(Color.BLACK);
        mArrowPaint.setStyle(Paint.Style.FILL);
        mArrowPaint.setAntiAlias(true);

        mScrollManager = new ScrollManager(this, getCount(), mLoop);
        mScrollManager.setCallback(this);
    }

    public void setCount(int count) {
        mScrollManager.setCount(count);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
        mBitmapHeight = (int) (mHeight * .5);
        // ensure mBitmapHeight is divisible by SIDES_PER_POSITION
        // how much are we cutting off with the planned integer division?
        int remainder = mBitmapHeight % SIDES_PER_POSITION;
        mBitmapHeight /= SIDES_PER_POSITION;
        // if we're cutting off more than 10 pixels, round up
        if (remainder > 10) mBitmapHeight++;
        mBitmapHeight *= SIDES_PER_POSITION;
        mBitmapWidth = mWidth - 20;

        /*
        NOTE REGARDING REGULAR POLYGONS:
        radius = distance from the centre of the polygon to any vertex
        apothem = distance from the centre of the polygon to the centre of any side
        (The more sides a regular polygon has, the closer the radius and the apothem will be.

        THE PICKER WHEEL:
        The wheel is a regular polygon (only visible sides are drawn). It has a number of positions
        and a number of sides per position.
        It needs to fill the view vertically, and will be positioned at a distance of the
        apothem behind the screen.

        TO CALCULATE THE SIZE OF THE WHEEL:
        The apothem is not yet known so approximate with half of the view height.
        Translate a length of 1000 a distance of half of the view height along the z-axis, in to
        the screen. This will scale the length.
        The inverse scale ratio (original length / scaled length) multiplied by the height of
        the view gives the required diameter of the wheel to fill the view.
        */

        // points represent a vertical length of 1000 passing through the origin at its midpoint
        float[] points = {
                0f, -500f,
                0f, 500f
        };
        Matrix m = new Matrix();
        Camera c = new Camera();
        c.save();
        // translate half height along z-axis
        c.translate(0, 0, h / 2);
        c.getMatrix(m);
        c.restore();
        // apply the matrix to the points
        m.mapPoints(points);
        // calculate inverseScaleRatio
        float inverseScaleRatio = 1000f / Math.abs(points[3] - points[1]);
        // multiply by height / 2 to get the radius
        float radius = h * inverseScaleRatio / 2;
        Log.v(TAG, "radius = " + radius);

        mNumberOfSides = (int) Math.round(Math.PI / Math.asin(mBitmapHeight / SIDES_PER_POSITION / (2 * radius)));
        radius = (float) (mBitmapHeight / SIDES_PER_POSITION / (2 * Math.sin(Math.PI / mNumberOfSides)));
        Log.v(TAG, "radius = " + radius);
        mApothem = (float) (radius * Math.cos(Math.PI / mNumberOfSides) - 1);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                width = mSize * 100;
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(width, mSize * 150);
                break;
            case MeasureSpec.EXACTLY:
                break;
            default:
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                height = mSize * 150;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(height, mSize * 150);
                break;
            case MeasureSpec.EXACTLY:
                break;
            default:
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centrePosition = Math.round(mScrollManager.getPosition());
        drawBitmap(canvas, centrePosition);

        if (mGestureActive) {

            int position;
            int count = mScrollManager.getCount();
            boolean exit = false;

            for (int i = 1; ; ) {
                position = centrePosition + i;
                boolean aboveLowerLimit = position >= 0;
                boolean belowUpperLimit = position <= count - 1;
                if ((mLoop || (aboveLowerLimit && belowUpperLimit))
                        && drawBitmap(canvas, position)) {
                    exit = false;
                } else {
                    if (exit) break;
                    exit = true;
                }
                if (i > 0) {
                    i = -i;
                } else {
                    i = -i + 1;
                }
            }
        } else {
            if (mLoop || mScrollManager.getPosition() > 0) {
                drawUpArrow(canvas);
            }
            if (mLoop || mScrollManager.getPosition() < mScrollManager.getCount() - 1) {
                drawDownArrow(canvas);
            }
        }
    }

    private boolean drawBitmap(Canvas canvas, int position) {
        // position is the position in the list or sequence of objects.
        // At this point, position may be "out of bounds", but absPosition (the visible position
        // on the wheel) should be calculated before correcting the range:
        float absPosition = position - mScrollManager.getPosition();
        // Now put position within the correct range if needed.
        position %= mScrollManager.getCount();
        if (position < 0) {
            position += mScrollManager.getCount();
        }

        float rollback = (SIDES_PER_POSITION / 2f) - .5f;
        float rotation = -absPosition * 360 / mNumberOfSides * SIDES_PER_POSITION;
        float prevRotation = -(absPosition - mScrollManager.getCount()) * 360 / mNumberOfSides * SIDES_PER_POSITION;
        float nextRotation = -(absPosition + mScrollManager.getCount()) * 360 / mNumberOfSides * SIDES_PER_POSITION;

        if ((rotation > 0 && setMatrix(mBitmapWidth, Math.round((float) mBitmapHeight / SIDES_PER_POSITION), rotation - (rollback * 360 / mNumberOfSides)))
                || (rotation <= 0 && setMatrix(mBitmapWidth, Math.round((float) mBitmapHeight / SIDES_PER_POSITION), rotation + (rollback * 360 / mNumberOfSides)))) {

            List<Bitmap> slices = mBitmaps.get(position);
            if (slices == null) {
                mBitmaps.put(position, new ArrayList<Bitmap>(SIDES_PER_POSITION));
                new BitmapsThread(position).start();
                return true;
            }
            float subRotation = rotation + (rollback * 360f / mNumberOfSides);
            for (int i = 0; i < slices.size(); i++) {
                if (setMatrix(mBitmapWidth, Math.round((float) mBitmapHeight / SIDES_PER_POSITION), subRotation)) {
                    int tint = Math.max(0, (int) (0xFF * ((subRotation)) / 120));
                    int mul = Color.rgb(1, 1, 1);
                    int add = Color.rgb(tint, tint, tint);
                    mBitmapPaint.setColorFilter(new LightingColorFilter(mul, add));

                    canvas.drawBitmap(slices.get(i), mMatrix, mBitmapPaint);
                }
                subRotation = subRotation - 360f / mNumberOfSides;
            }
            return true;
        } else if (prevRotation > 90 && nextRotation < -90) {
            mBitmaps.remove(position);
        }
        return false;
    }

    public void drawUpArrow(Canvas canvas) {
        setMatrix(mBitmapWidth, mBitmapHeight, 360f / mNumberOfSides * SIDES_PER_POSITION);
        canvas.drawBitmap(getArrow(UP_ARROW), mMatrix, mArrowPaint);
    }

    public void drawDownArrow(Canvas canvas) {
        setMatrix(mBitmapWidth, mBitmapHeight, -360f / mNumberOfSides * SIDES_PER_POSITION);
        canvas.drawBitmap(getArrow(DOWN_ARROW), mMatrix, mArrowPaint);
    }

    private Bitmap getArrow(int direction) {
        Bitmap b = Bitmap.createBitmap(getBitmapWidth(), getBitmapHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        switch (direction) {
            case UP_ARROW:

                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(getBitmapWidth() * 2 / 5, getBitmapHeight());
                path.lineTo(getBitmapWidth() / 2, getBitmapHeight() * 2 / 3);
                path.lineTo(getBitmapWidth() * 3 / 5, getBitmapHeight());
                path.lineTo(getBitmapWidth() * 2 / 5, getBitmapHeight());
                path.close();

                c.drawPath(path, mArrowPaint);
                break;
            case DOWN_ARROW:

                path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(getBitmapWidth() * 2 / 5, 0);
                path.lineTo(getBitmapWidth() / 2, getBitmapHeight() / 3);
                path.lineTo(getBitmapWidth() * 3 / 5, 0);
                path.lineTo(getBitmapWidth() * 2 / 5, 0);
                path.close();

                c.drawPath(path, mArrowPaint);
                break;
        }
        return b;
    }

    protected abstract Bitmap getBitmap(int position);

    private boolean setMatrix(int bitmapWidth, int bitmapHeight, float rotation) {

        mCamera.save();

        mCamera.translate(0, 0, mApothem);
        mCamera.rotateX(rotation);
        mCamera.translate(0, 0, -mApothem);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(-bitmapWidth / 2, -bitmapHeight / 2);
        mMatrix.postTranslate(mWidth / 2, mHeight / 2);

        float[] points = {
                0, 0,
                0, 100,
        };
        mMatrix.mapPoints(points);
        return points[1] < points[3];
    }

    public int getBitmapWidth() {
        return mBitmapWidth;
    }

    public int getBitmapHeight() {
        return mBitmapHeight;
    }

    public float getScrollPosition() {
        return mScrollManager.getPosition();
    }

    public void setScrollPosition(float position) {
        mScrollManager.setPosition(position);
        invalidate();
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
            default:
                mGestureActive = true;
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
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScrollManager.fling(-velocityY / 30000);
            return true;
        }

    }

    private class BitmapsThread extends Thread {

        private int mPosition;

        public BitmapsThread(int position) {
            mPosition = position;
        }

        public void run() {
            List<Bitmap> slices;
            while ((slices = mBitmaps.get(mPosition)) == null) ;
            Bitmap b = getBitmap(mPosition);
            for (int i = 0; i < SIDES_PER_POSITION; i++) {
                slices.add(Bitmap.createBitmap(b, 0, i * mBitmapHeight / SIDES_PER_POSITION,
                        mBitmapWidth, Math.round((float) mBitmapHeight / SIDES_PER_POSITION)));
            }
            mBitmaps.put(mPosition, slices);
            postInvalidate();
        }
    }

}
