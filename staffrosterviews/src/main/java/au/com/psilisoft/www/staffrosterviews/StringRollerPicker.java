package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fletcher on 20/09/2015.
 */
public class StringRollerPicker extends RollerPicker {

    private List<String> mList;
    private Paint mTextPaint;

    public StringRollerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        mList = new ArrayList<>();
        mList.add("One");
        mList.add("Two");
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        super.init(context, attrs);
    }

    @Override
    protected int getCount() {
        return mList.size();
    }

    @Override
    protected Rect getBitmapSize() {
        return null;
    }

    @Override
    protected Bitmap getBitmap(int position) {
        Bitmap b = Bitmap.createBitmap(getBitmapWidth(), getBitmapHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        mTextPaint.setTextSize(getBitmapHeight() / 1.2f);
        Rect bounds = new Rect();
        //mTextPaint.getTextBounds(String.valueOf(mList.get(position)), 0, mList.get(position).length(), bounds);
        while (true) {
            mTextPaint.getTextBounds(String.valueOf(mList.get(position)), 0, mList.get(position).length(), bounds);
            if (bounds.width() > getBitmapWidth() / 1.2f) {
                mTextPaint.setTextSize(mTextPaint.getTextSize() - 10);
            } else {
                break;
            }
        }
        c.drawText(mList.get(position), getBitmapWidth() / 2, getBitmapHeight() / 2 + bounds.height() / 2, mTextPaint);

        return b;
    }

    @Override
    public void newPosition(float position) {
        invalidate();
    }

    @Override
    public void stopped(int position) {
        setGestureActive(false);
        invalidate();
    }

    @Override
    public void looped(int direction) {

    }
}
