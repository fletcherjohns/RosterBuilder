package au.com.psilisoft.www.staffrosterviews;

import android.util.Log;

/**
 * Created by Fletcher on 17/09/2015.
 */
public class ScrollManager {

    public static final int LOOP_FORWARD = 1;
    public static final int LOOP_BACKWARDS = 2;
    private static final float FLING_VELOCITY_THRESHOLD = 0.01f;

    private int mCount;
    private float mPosition;
    private float mVelocity;
    private boolean mLoop;

    private ScrollCallback mCallback;
    private Thread mThread;

    public ScrollManager(int count, boolean loop) {
        mCount = count;
        mLoop = loop;
    }

    public ScrollManager(int count) {
        this(count, false);
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
        if (mLoop || (mPosition + distance >= 0 && mPosition + distance <= mCount - 1)) {
            mPosition += distance;

            if (mCallback != null) {
                if (mPosition >= mCount) {
                    mCallback.looped(LOOP_FORWARD);
                } else if (mPosition < 0) {
                    mCallback.looped(LOOP_BACKWARDS);
                }
            }

            mPosition %= mCount;
            if (mPosition < 0) {
                mPosition += mCount;
            }
            if (mCallback != null) mCallback.newPosition();
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
                mCallback.stopped();
            }
        }
    }

    /**
     * IMPORTANTE MI AMIGO!!!! These callbacks may be running in a background thread.
     */
    interface ScrollCallback {
        void newPosition();
        void stopped();
        void looped(int direction);
    }
}
