package au.com.psilisoft.www.rosterbuilder.custom_objects;

import java.util.Comparator;

/**
 * Created by Fletcher on 26/08/2015.
 */
public class StaffPossibleShiftComparator implements Comparator<Staff> {
    @Override
    public int compare(Staff lhs, Staff rhs) {
        return lhs.getPossibleShifts().size() - rhs.getPossibleShifts().size();
    }
}
