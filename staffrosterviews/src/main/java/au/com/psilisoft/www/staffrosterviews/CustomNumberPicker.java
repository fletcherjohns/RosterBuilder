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
    private int mBitmapEdge;

    private Paint mTextPaint;
    private Paint mBitmapPaint;
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
        mBitmapEdge = (int) (mHeight / Math.sqrt(4 + 2 * Math.sqrt(2)));
        mTextPaint.setTextSize(mBitmapEdge / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.CYAN);
        int value;
        int firstPosition = (int) Math.round(mScrollPosition - 1);
        int lastPosition = (int) Math.round(mScrollPosition + 1);

        for (int position = firstPosition; position <= lastPosition; position++) {

            value = position % mCount;
            if (value < 0) {
                value += mCount;
            }
            value *= mIncrement;

            Bitmap b = getBitmap(value);
            setMatrix(position);
            canvas.drawBitmap(b, mMatrix, null);
        }
    }


    private Bitmap getBitmap(int value) {

        Random r = new Random();
        Bitmap b = Bitmap.createBitmap(mBitmapEdge, mBitmapEdge, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawColor(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
        float textHeight = mTextPaint.getTextSize();
        c.drawText(String.valueOf(value), mBitmapEdge / 2, mBitmapEdge / 2 + textHeight / 2, mTextPaint);
        return b;
    }

    private void setMatrix(int position) {

        RectF rect = getChildDimensions();

        float rotation = (float) (mScrollPosition - position) * 45;
        int radius = (int) (mBitmapEdge * (1 + Math.sqrt(2)) / 2);
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
