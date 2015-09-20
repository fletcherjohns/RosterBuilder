package au.com.psilisoft.www.staffrosterviews.scrollmanager;

import android.view.View;

/**
 * Created by Fletcher on 17/09/2015.
 * <p/>
 * This class manages the scroll position and velocity of an {@link android.widget.AdapterView} or
 * any custom scrollable view. Please note that
 */
public class ScrollManager implements ScrollCallback {

    public static final int LOOP_FORWARD = 1;
    public static final int LOOP_BACKWARDS = 2;
    private static final float FLING_VELOCITY_THRESHOLD = 0.01f;

    private View mView;
    private int mCount;
    private float mPosition;
    private float mVelocity;
    private boolean mLoop;

    private ScrollCallback mCallback;
    private Thread mThread;

    public ScrollManager(View view, int count, boolean loop) {
        mView = view;
        mCount = count;
        mLoop = loop;
    }

    public ScrollManager(View view, int count) {
        this(view, count, false);
    }

    public void setCallback(ScrollCallback callback) {
        mCallback = callback;
    }

    public int getCount() {
        return mCount;
    }

    public float getPosition() {
        return mPosition;
    }

    public void setPosition(float position) {
        mPosition = position;
    }

    public float getVelocity() {
        return mVelocity;
    }

    public void setVelocity(float velocity) {
        mVelocity = velocity;
    }

    public void interrupt() {

        mVelocity = 0;
        if (mThread != null) mThread.interrupt();
    }

    public void scroll(float distance) {
        if (!mLoop) {
            if (mPosition + distance < 0) {
                distance = -mPosition;
                mVelocity = 0;
            }
            if (mPosition + distance > mCount - 1) {
                distance = mCount - 1 - mPosition;
                mVelocity = 0;
            }
        }
        mPosition += distance;

        // If mPosition is outside of the range 0 to mCount - 1, send loop callback
        if (mCallback != null) {
            if (mPosition < 0 || mPosition >= mCount) {
                int direction = LOOP_FORWARD;
                if (mPosition < 0) {
                    direction = LOOP_BACKWARDS;
                }
                looped(direction);
            }
        }

        mPosition %= mCount;
        if (mPosition < 0) {
            mPosition += mCount;
        }
        if (mCallback != null) {
            newPosition(mPosition);
        }

    }

    public void fling(float velocity) {
        mVelocity = velocity;
        mThread = new FlingThread();
        mThread.start();
    }

    public void ensureThreadIsAlive() {

        if (mThread == null || !mThread.isAlive()) {
            mThread = new FlingThread();
            mThread.start();
        }
    }

    @Override
    public void newPosition(final float position) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                mCallback.newPosition(position);
            }
        });
    }

    @Override
    public void stopped(final int position) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                mCallback.stopped(position);
            }
        });
    }

    @Override
    public void looped(final int direction) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                mCallback.looped(direction);
            }
        });
    }

    private class FlingThread extends Thread {

        @Override
        public void run() {

            while (Math.abs(mVelocity) > FLING_VELOCITY_THRESHOLD) {
                mVelocity *= 0.97f;

                scroll(mVelocity);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }
            int snapPosition = Math.round(mPosition);
            while (Math.abs((snapPosition - mPosition)) > 0.001) {
                mVelocity = (snapPosition - mPosition) / 10;
                scroll(mVelocity);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }
            mVelocity = 0;
            scroll(snapPosition - mPosition);
            if (mCallback != null) {
                stopped((int) mPosition);
            }
        }
    }

}
