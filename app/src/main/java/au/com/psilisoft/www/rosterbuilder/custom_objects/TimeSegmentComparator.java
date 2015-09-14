package au.com.psilisoft.www.rosterbuilder.custom_objects;

import android.util.Log;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by Fletcher on 26/08/2015.
 */
public class TimeSegmentComparator implements Comparator<TimedSegment> {

    @Override
    public int compare(TimedSegment lhs, TimedSegment rhs) {

        Log.v("", lhs.getDay() + "," + rhs.getDay() + ", " + lhs.isShift() + ", " + rhs.isShift());
        if (lhs.getDay() == rhs.getDay() && lhs.isShift() && !rhs.isShift()) {
            return 1;
        } else {
            return (int) (lhs.getStartTime().getTimeInMillis() - rhs.getStartTime().getTimeInMillis());
        }
    }
}
