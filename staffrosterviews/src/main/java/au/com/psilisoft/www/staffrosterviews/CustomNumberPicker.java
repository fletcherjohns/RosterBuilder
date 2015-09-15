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
import android.graphics.RectF;
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
    private int mCount;
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
        mCount = (mMax - mMin) / mIncrement;
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mCamera = new Camera();
        mMatrix = new Matrix();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mWidth = r - l;
        mHeight = b - t;
        mBitmapEdge = (int) (mHeight / Math.sqrt(4 + 2 * Math.sqrt(2)));
        mPaint.setTextSize(mBitmapEdge / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int position;
        int firstPosition = (int) Math.round(mScrollPosition - 2);
        int lastPosition = (int) Math.round(mScrollPosition + 2);
        double distanceFromCentre;

        for (int i = firstPosition; i <= lastPosition; i += mIncrement) {
            distanceFromCentre = mScrollPosition - i;

            Bitmap b = getBitmap(i % mCount);
            setMatrix(i);
            canvas.drawBitmap(b, mMatrix, null);
        }
    }


    private Bitmap getBitmap(int position) {

        Bitmap b = Bitmap.createBitmap(mBitmapEdge, mBitmapEdge, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawText(String.valueOf(mMin + (position * mIncrement)), mBitmapEdge / 2, mBitmapEdge / 2, mPaint);
        return b;
    }

    private void setMatrix(int position) {

        RectF rect = getChildDimensions();

        float rotation = (float) ((mScrollPosition - position) * Math.PI / 4);
        int radius = mHeight / 2;
        mCamera.save();
        mCamera.translate(0, 0, radius);
        mCamera.rotateX(rotation);
        mCamera.translate(0, 0, -radius);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(-mBitmapEdge / 2, -mBitmapEdge / 2);
        mMatrix.postTranslate(mBitmapEdge / 2 + rect.left, mBitmapEdge / 2 + rect.top);
    }

    private RectF getChildDimensions() {

        int halfFrameWidth = mWidth / 2;
        int halfFrameHeight = mHeight / 2;
        int halfChildWidth = mBitmapEdge / 2;
        int halfChildHeight = mBitmapEdge / 2;

        // Centre the view horizontally and vertically
        int left = halfFrameWidth - halfChildWidth;
        int top = halfFrameHeight - halfChildHeight;

        return new RectF(left, top, left + mBitmapEdge, top + mBitmapEdge);
    }
}
