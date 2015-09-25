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
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import au.com.psilisoft.www.staffrosterviews.scrollmanager.ScrollCallback;
import au.com.psilisoft.www.staffrosterviews.scrollmanager.ScrollManager;

/**
 * Created by Fletcher on 20/09/2015.
 */
public abstract class RollerPicker extends View implements ScrollCallback {

    private static final String TAG = "tag";
    private static final int UP_ARROW = -1;
    private static final int DOWN_ARROW = -2;
    private static final String SUPER_INSTANCE_STATE = "super_instance_state";
    private static final String STATE_POSITION = "state_position";

    private int mNumberOfSides;
    private boolean mLoop;

    private int mWidth;
    private int mHeight;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mApothem;

    private Camera mCamera;
    private Matrix mMatrix;
    private HashMap<Integer, Bitmap> mBitmaps;
    private Paint mBitmapPaint;
    private Paint mArrowPaint;

    private GestureDetector mGestureDetector;
    private boolean mGestureActive = false;
    private ScrollManager mScrollManager;

    public RollerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.RollerPicker, 0, 0);
        try {
            mLoop = a.getBoolean(R.styleable.RollerPicker_Loop, false);
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

    public void setLoop(boolean loop) {
        mScrollManager.setLoop(mLoop = loop);
        invalidate();
    }

    public boolean isGestureActive() {
        return mGestureActive;
    }

    public void setGestureActive(boolean gestureActive) {
        mGestureActive = gestureActive;
    }

    public void setCount(int count) {
        mScrollManager.setCount(count);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
        // The reverse of these are used in onMeasure
        mBitmapHeight = (int) (mHeight * .5);
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

        mNumberOfSides = (int) Math.round(Math.PI / Math.asin(mBitmapHeight / (2 * radius)));
        radius = (float) (mBitmapHeight / (2 * Math.sin(Math.PI / mNumberOfSides)));
        Log.v(TAG, "radius = " + radius);
        mApothem = (float) (radius * Math.cos(Math.PI / mNumberOfSides) - 1);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // The desired size tries to fit the bitmap which is
        // the responsibility of classes overriding getBitmapSize()
        // to measure and create...
        Rect rect = getBitmapSize();
        int desiredWidth = rect.width() + 20;
        int desiredHeight = rect.height() * 2;

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                widthSize = desiredWidth;
                break;
            case MeasureSpec.AT_MOST:
                widthSize = Math.min(widthSize, desiredWidth);
                break;
            case MeasureSpec.EXACTLY:
                break;
            default:
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                heightSize = desiredHeight;
                break;
            case MeasureSpec.AT_MOST:
                heightSize = Math.min(heightSize, desiredHeight);
                break;
            case MeasureSpec.EXACTLY:
                break;
            default:
        }
        setMeasuredDimension(widthSize, heightSize);
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
        if (mScrollManager.getCount() == 0) return false;
        position %= mScrollManager.getCount();
        if (position < 0) {
            position += mScrollManager.getCount();
        }

        float rotation = -absPosition * 360 / mNumberOfSides;
        float prevRotation = -(absPosition - mScrollManager.getCount()) * 360 / mNumberOfSides;
        float nextRotation = -(absPosition + mScrollManager.getCount()) * 360 / mNumberOfSides;

        if (setMatrix(rotation)) {
            Bitmap b = mBitmaps.get(position);
            if (b == null) {
                mBitmaps.put(position, Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8));
                new BitmapsThread(position).start();
                return true;
            }
            int tint = Math.max(0, (int) (0xFF * ((rotation)) / 120));
            int mul = Color.rgb(1, 1, 1);
            int add = Color.rgb(tint, tint, tint);
            mBitmapPaint.setColorFilter(new LightingColorFilter(mul, add));

            canvas.drawBitmap(b, mMatrix, mBitmapPaint);
            return true;
        } else if (prevRotation > 90 && nextRotation < -90) {
            mBitmaps.remove(position);
        }
        return false;
    }

    public void drawUpArrow(Canvas canvas) {
        setMatrix(360f / mNumberOfSides);
        canvas.drawBitmap(getArrow(UP_ARROW), mMatrix, mArrowPaint);
    }

    public void drawDownArrow(Canvas canvas) {
        setMatrix(-360f / mNumberOfSides);
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

    protected abstract Rect getBitmapSize();
    protected abstract Bitmap getBitmap(int position);

    private boolean setMatrix(float rotation) {

        mCamera.save();

        mCamera.translate(0, 0, mApothem);
        mCamera.rotateX(rotation);
        mCamera.translate(0, 0, -mApothem);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(-mBitmapWidth / 2, -mBitmapHeight / 2);
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
            mBitmaps.put(mPosition, getBitmap(mPosition));
            postInvalidate();
        }
    }

}
