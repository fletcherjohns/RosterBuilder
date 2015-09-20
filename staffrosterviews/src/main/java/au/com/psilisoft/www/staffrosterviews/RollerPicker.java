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
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import au.com.psilisoft.www.staffrosterviews.scrollmanager.ScrollCallback;
import au.com.psilisoft.www.staffrosterviews.scrollmanager.ScrollManager;

/**
 * Created by Fletcher on 20/09/2015.
 */
public abstract class RollerPicker extends View implements ScrollCallback {

    private static final int SIZE_SMALL = 1;
    private static final int SIZE_MEDIUM = 2;
    private static final int SIZE_LARGE = 3;
    private static final String TAG = "tag";
    private static final int UP_ARROW = -1;
    private static final int DOWN_ARROW = -2;
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



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
        mBitmapHeight = (int) (mHeight * .5);
        mBitmapWidth = mWidth - 50;

        float[] points = {
                0f, 0f,
                0f, (float) h
        };
        Matrix m = new Matrix();
        Camera c = new Camera();
        c.save();
        c.translate(0, 0, h / 2);
        c.getMatrix(m);
        c.restore();
        m.preTranslate(0, -(h / 2));
        m.postTranslate(0, h / 2);
        m.mapPoints(points);

        float radius = h * h / Math.abs(points[3] - points[1]) / 2;
        mNumberOfSides = (int) (Math.PI / Math.asin(mBitmapHeight / (2 * radius)));
        mApothem = (float) (radius * Math.cos(Math.PI / mNumberOfSides));

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
                width = Math.min(width, mSize * 100);
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

        drawBitmap(canvas, 0);

        if (mGestureActive) {

            int position = Math.round(mScrollManager.getPosition());
            int count = mScrollManager.getCount();
            boolean exit = false;

            for (int i = 1; ; ) {
                boolean aboveLowerLimit = i + position >= 0;
                boolean belowUpperLimit = i + position <= count - 1;
                if ((mLoop || (aboveLowerLimit && belowUpperLimit))
                        && drawBitmap(canvas, i)) {
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
            if (mLoop || mScrollManager.getPosition() > 0) drawUpArrow(canvas);
            if (mLoop || mScrollManager.getPosition() < mScrollManager.getCount() - 1) drawDownArrow(canvas);
            Iterator<Map.Entry<Integer, Bitmap>> i = mBitmaps.entrySet().iterator();
            while (i.hasNext()) {
                if (!setMatrix((int) (i.next().getKey() - mScrollManager.getPosition()))) {
                    i.remove();
                }
            }
        }
    }

    private boolean drawBitmap(Canvas canvas, int position) {

        Log.v(TAG, "drawBitmap(canvas, " + position + ")");
        int centrePosition = Math.round(mScrollManager.getPosition());
        int absPosition = (centrePosition + position) % mScrollManager.getCount();
        if (absPosition < 0) {
            absPosition += mScrollManager.getCount();
        }

        if (setMatrix(position)) {

            Bitmap b = mBitmaps.get(absPosition);
            if (b == null) {
                Log.v("lll", "loading bitmap");
                b = getBitmap(absPosition);
                mBitmaps.put(absPosition, b);
            } else {
                Log.v("lll", "already had bitmap");
            }
            canvas.drawBitmap(b, mMatrix, mBitmapPaint);
            Log.v(TAG, "mBitmaps.size() = " + mBitmaps.size());
            return true;
        } else {
            mBitmaps.remove(absPosition);
            Log.v(TAG, "mBitmaps.size() = " + mBitmaps.size());
            return false;
        }

    }

    public void drawUpArrow(Canvas canvas) {
        setMatrix(-1);
        canvas.drawBitmap(getArrow(UP_ARROW), mMatrix, mArrowPaint);
    }

    public void drawDownArrow(Canvas canvas) {
        setMatrix(1);
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

    private boolean setMatrix(int position) {

        float offset = mScrollManager.getPosition() - Math.round(mScrollManager.getPosition());
        float rotation = -(position - offset) * 360 / mNumberOfSides;
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
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScrollManager.fling(-velocityY / 30000);
            return true;
        }

    }

}
