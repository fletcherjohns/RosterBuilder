package au.com.psilisoft.www.rosterbuilder.custom_objects;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.psilisoft.www.rosterbuilder.provider.DatabaseManager;

/**
 * Created by Fletcher on 21/08/2015.
 */
public class Roster {

    private static final String TAG = "roster_output";
    private Context mContext;
    private long mRosterId;
    private List<Ability> mAbilities;
    private List<Shift> mShifts;
    private List<TeamRequirement> mTeamRequirements;
    private List<Staff> mStaff;

    private RosterInterface mInterface;

    public Roster(Context context, long rosterId) {

        try {
            mInterface = (RosterInterface) context;
        } catch (ClassCastException e) {
            Log.e("", "Need to implement RosterInterface");
        }
        mContext = context;
        mRosterId = rosterId;
        mAbilities = DatabaseManager.getAbilities(mContext);
        mStaff = DatabaseManager.getStaff(mContext, mAbilities);
        mShifts = new ArrayList<>();
        mTeamRequirements = new ArrayList<>();
        updateRosterSegments();

        setShiftStaff();
        resetTeamRequirements();
        setStaffPossibleShifts();
    }

    private void setShiftStaff() {

        Staff staff;
        for (Shift shift : mShifts) {
            staff = DatabaseManager.getShiftStaff(mContext, shift.getId(), mStaff);
            if (staff != null) {
                shift.setStaff(staff);
                staff.addRosteredShift(shift);
            }
        }
    }

    private List<Shift> getShiftsAtTime(long timeMillis, Ability ability) {
        List<Shift> list = new ArrayList<>();

        for (Shift shift : mShifts) {
            if (shift.existsAtTime(timeMillis)
                    && (shift.getStaff() == null || shift.getStaff().getAbilities().contains(ability))) {
                list.add(shift);
            }
        }
        return list;
    }

    private List<TeamRequirement> getTeamRequirementsAtTime(long timeMillis) {
        List<TeamRequirement> list = new ArrayList<>();

        for (TeamRequirement requirement : mTeamRequirements) {
            if (requirement.existsAtTime(timeMillis)) {
                list.add(requirement);
            }
        }
        return list;
    }

    private void resetTeamRequirements() {
        for (Shift shift : mShifts) {
            shift.setAbilities(DatabaseManager
                    .getRosterSegmentAbilities(mContext, shift.getId(), mAbilities));
        }
        enforceTeamRequirements();
    }

    private void enforceTeamRequirements() {

        long startTime;
        long finishTime;

        List<TeamRequirement> teamRequirementsAtStart;
        List<TeamRequirement> teamRequirementsAtFinish;
        List<Shift> shiftsAtStart;
        List<Shift> shiftsAtFinish;
        List<Ability> abilities = new ArrayList<>();
        for (Shift shift : mShifts) {

            startTime = shift.getStartTime().getTimeInMillis();
            teamRequirementsAtStart = getTeamRequirementsAtTime(startTime);


            for (TeamRequirement requirement : teamRequirementsAtStart) {
                abilities.addAll(requirement.getAbilities());
            }
            while (abilities.size() > 0) {
                shiftsAtStart = getShiftsAtTime(startTime, abilities.get(0));
                if (shiftsAtStart.size() == Collections.frequency(abilities, abilities.get(0))) {
                    for (Shift shiftt : shiftsAtStart) {
                        shiftt.addAbility(abilities.get(0));
                    }
                }
                abilities.removeAll(Collections.singleton(abilities.get(0)));
            }

            finishTime = shift.getFinishTime().getTimeInMillis();
            teamRequirementsAtFinish = getTeamRequirementsAtTime(finishTime);

            for (TeamRequirement requirement : teamRequirementsAtFinish) {
                abilities.addAll(requirement.getAbilities());
            }
                while (abilities.size() > 0) {
                    shiftsAtFinish = getShiftsAtTime(finishTime, abilities.get(0));
                    if (shiftsAtFinish.size() == Collections.frequency(abilities, abilities.get(0))) {
                        for (Shift shiftt : shiftsAtFinish) {
                            shiftt.addAbility(abilities.get(0));
                        }
                    }
                    abilities.removeAll(Collections.singleton(abilities.get(0)));
                }

        }
    }

    private void setStaffPossibleShifts() {

        for (Staff staff : mStaff) {
            for (Shift shift : mShifts) {
                if (staff.canDo(shift)) {
                    staff.getPossibleShifts().add(shift);
                    shift.getPossibleStaff().add(staff);
                } else {
                    staff.getPossibleShifts().remove(shift);
                    shift.getPossibleStaff().remove(staff);
                }
            }
        }
        Collections.sort(mStaff, new StaffPossibleShiftComparator());
        Collections.sort(mShifts, new ShiftPossibleStaffComparator());
    }

    public void fillShifts() {

        List<Shift> empty_shifts = new ArrayList<>();
        for (Shift shift : mShifts) {
            if (shift.getStaff() == null) {
                empty_shifts.add(shift);
            }
        }
        boolean fillToMax = false;
        loop:
        while (true) {
            resetTeamRequirements();
            setStaffPossibleShifts();

            Collections.sort(empty_shifts, new ShiftPossibleStaffComparator());
            Collections.sort(mStaff, new StaffRequiredShiftComparator());
            Collections.sort(mStaff, new StaffPossibleShiftComparator());

            Log.v(TAG, "Shifts: " + empty_shifts.toString());
            Log.v(TAG, "Staff: " + mStaff.toString());
            for (Staff staff : mStaff) {
                if ((fillToMax ? !staff.hasMaxShifts() : !staff.hasMinShifts())) {

                    for (Shift shift : empty_shifts) {
                        if (shift.getStaff() == null && shift.getPossibleStaff().size() > 0
                                && staff.getPossibleShifts().contains(shift)) {
                            Log.v(TAG, "rostering " + staff.getName()
                                    + " for shift " + shift.getStartTime().getTime().toString());
                            shift.setStaff(staff);
                            staff.addRosteredShift(shift);
                            empty_shifts.remove(shift);
                            mInterface.notifyDataSetChanged();
                            continue loop;
                        }
                    }
                }
            }
            if (fillToMax) {
                Log.v(TAG, "Finished!!!");
                return;
            } else {
                fillToMax = true;
            }
        }
    }

    public List<TimedSegment> getRosterSegments() {
        List<TimedSegment> list = new ArrayList<>();
        list.addAll(mShifts);
        list.addAll(mTeamRequirements);
        return list;
    }

    public boolean updateRosterSegments() {


        List<Shift> shifts = DatabaseManager.getRosterShifts(mContext, mRosterId, mAbilities);
        List<TeamRequirement> requirements = DatabaseManager.getRosterTeamRequirements(mContext, mRosterId, mAbilities);
        if (shifts.size() != mShifts.size() || requirements.size() != mTeamRequirements.size()) {
            mShifts = shifts;
            mTeamRequirements = requirements;
            clearStaffPossibleShifts();
            setStaffPossibleShifts();
            return true;
        } else {
            return false;
        }
    }

    private void clearStaffPossibleShifts() {
        for (Shift shift : mShifts) {
            shift.getPossibleStaff().clear();
        }
        for (Staff staff : mStaff) {
            staff.getPossibleShifts().clear();
        }
    }

    public interface RosterInterface {
        void notifyDataSetChanged();
    }
}
