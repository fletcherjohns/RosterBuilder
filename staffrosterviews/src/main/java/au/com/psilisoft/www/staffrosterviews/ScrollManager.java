package au.com.psilisoft.www.staffrosterviews;

/**
 * Created by Fletcher on 17/09/2015.
 */
class ScrollManager {

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

    public void setCount(int count) {
        mCount = count;
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

    public void start() {

        if (mThread != null) mThread.interrupt();
    }

    public void scroll(float distance) {
        mPosition += distance;
        mPosition %= mCount;
        mCallback.newPosition();
    }

    public void fling(float velocity) {
        mVelocity = velocity;
        mThread = new FlingThread();
        mThread.start();
    }

    public void stop() {

        if (mThread == null || !mThread.isAlive()) {
            mThread = new FlingThread();
            mThread.start();
        }
    }

    private class FlingThread extends Thread {

        @Override
        public void run() {

            while (Math.abs(mVelocity) > FLING_VELOCITY_THRESHOLD) {
                mVelocity *= 0.99f;

                mPosition += mVelocity;
                mPosition %= mCount;
                mCallback.newPosition();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }
            int snapPosition = Math.round(mPosition);
            while (Math.abs((snapPosition - mPosition)) > 0.001) {
                mVelocity = (snapPosition - mPosition) / 10;
                mPosition += mVelocity;
                mCallback.newPosition();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }
            mVelocity = 0;
            mPosition = snapPosition;
            mCallback.newPosition();
            mCallback.stopped();
        }
    }

    interface ScrollCallback {
        void newPosition();
        void stopped();
    }
}
