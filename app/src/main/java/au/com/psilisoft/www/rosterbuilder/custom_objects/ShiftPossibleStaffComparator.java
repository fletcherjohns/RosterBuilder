package au.com.psilisoft.www.rosterbuilder.custom_objects;

import java.util.Comparator;

/**
 * Created by Fletcher on 26/08/2015.
 */
public class ShiftPossibleStaffComparator implements Comparator<Shift> {
    @Override
    public int compare(Shift lhs, Shift rhs) {
        return lhs.getPossibleStaff().size() - rhs.getPossibleStaff().size();
    }
}
