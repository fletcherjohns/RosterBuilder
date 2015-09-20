package au.com.psilisoft.www.staffrosterviews.scrollmanager;

/**
 * Created by Fletcher on 20/09/2015.
 */
public interface ScrollCallback {

    void newPosition(float position);

    void stopped(int position);

    void looped(int direction);

}
