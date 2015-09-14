package au.com.psilisoft.www.rosterbuilder.custom_objects;

import android.database.Cursor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Fletcher on 16/08/2015.
 */
public class Shift extends TimedSegment {

    private Staff mStaff;
    private Set<Staff> mPossibleStaff;

    public Shift(Cursor main) {
        this(main, null);
    }

    public Shift(Cursor main, Cursor abilities) {
        super(main, abilities);
        mPossibleStaff = new HashSet<>();
    }

    public Staff getStaff() {
        return mStaff;
    }

    public Set<Staff> getPossibleStaff() {
        return mPossibleStaff;
    }

    public void setStaff(Staff staff) {
        mStaff = staff;
    }

    @Override
    public boolean isShift() {
        return true;
    }

    @Override
    public String toString() {
        DateFormat format = new SimpleDateFormat("hh:mm EE dd/MM/yy");
        return format.format(getStartTime().getTime()) + "(" + mPossibleStaff.size() + " possible staff)";
    }
}
