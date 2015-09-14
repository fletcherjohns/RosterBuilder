package au.com.psilisoft.www.rosterbuilder.custom_objects;

import java.util.Comparator;

/**
 * Created by Fletcher on 29/08/2015.
 */
public class StaffRequiredShiftComparator implements Comparator<Staff> {

    @Override
    public int compare(Staff lhs, Staff rhs) {
        return (lhs.getMinShifts() - lhs.getRosteredShifts().size())
                - (rhs.getMinShifts() - rhs.getRosteredShifts().size());
    }
}
