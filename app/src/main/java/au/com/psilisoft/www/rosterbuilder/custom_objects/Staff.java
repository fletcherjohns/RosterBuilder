package au.com.psilisoft.www.rosterbuilder.custom_objects;

import android.database.Cursor;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;

/**
 * Created by Fletcher on 21/08/2015.
 */
public class Staff {

    private long mId;
    private String mName;
    private int mMinShifts;
    private int mMaxShifts;
    private int mMinHours;
    private int mMaxHours;
    private Set<Ability> mAbilities;
    private Set<Availability> mAvailabilities;
    private Set<Calendar> mUnavailabilities;

    private Set<Shift> mPossibleShifts;
    private Set<Shift> mRosteredShifts;


    public Staff(Cursor main) {

        if (main != null) {
            mId = main.getLong(main.getColumnIndex(Contract.Staff.ID));
            mName = main.getString(main.getColumnIndex(Contract.Staff.NAME));
            mMinShifts = main.getInt(main.getColumnIndex(Contract.Staff.MIN_SHIFTS));
            mMaxShifts = main.getInt(main.getColumnIndex(Contract.Staff.MAX_SHIFTS));
            mMinHours = main.getInt(main.getColumnIndex(Contract.Staff.MIN_HOURS));
            mMaxHours = main.getInt(main.getColumnIndex(Contract.Staff.MAX_HOURS));
        } else {
            mId = -1;
            mName = "New Item";
            mMinShifts = 0;
            mMaxShifts = 0;
            mMinHours = 0;
            mMaxHours = 0;
        }
        mAbilities = new HashSet<>();
        mAvailabilities = new HashSet<>();
        mUnavailabilities = new HashSet<>();
        mPossibleShifts = new HashSet<>();
        mRosteredShifts = new HashSet<>();
    }


    public Set<Calendar> getUnavailabilities() {
        return mUnavailabilities;
    }

    public void setUnavailabilities(Collection<Calendar> unavailabilities) {
        mUnavailabilities.clear();
        mUnavailabilities.addAll(unavailabilities);
    }

    public void setAbilities(Collection<Ability> abilities) {
        mAbilities.clear();
        mAbilities.addAll(abilities);
    }

    public void setAvailabilities(Collection<Availability> availabilities) {
        mAvailabilities.clear();
        mAvailabilities.addAll(availabilities);
    }

    public Set<Shift> getRosteredShifts() {
        return mRosteredShifts;
    }

    public void setRosteredShifts(Set<Shift> rosteredShifts) {
        mRosteredShifts = rosteredShifts;
    }

    public void addRosteredShift(Shift shift) {
        mRosteredShifts.add(shift);
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getMinShifts() {
        return mMinShifts;
    }

    public int getMaxShifts() {
        return mMaxShifts;
    }

    public int getMinHours() {
        return mMinHours;
    }

    public int getMaxHours() {
        return mMaxHours;
    }

    public Set<Ability> getAbilities() {
        return mAbilities;
    }

    public Set<Availability> getAvailabilities() {
        return mAvailabilities;
    }

    public Set<Shift> getPossibleShifts() {
        return mPossibleShifts;
    }

    public boolean canDo(Shift shift) {

        return isAble(shift) && hasNoClash(shift) && isAvailable(shift);
    }

    private boolean isAble(Shift shift) {
        Set<Ability> shiftAbilities = shift.getAbilities();
        for (Ability shiftAbility : shiftAbilities) {
            if (!mAbilities.contains(shiftAbility)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasNoClash(Shift shift) {
        for (Shift rosteredShift : mRosteredShifts) {
            if (rosteredShift.doesClash(shift)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAvailable(Shift shift) {

        for (Calendar unavailability : mUnavailabilities) {
            if (unavailability.get(Calendar.YEAR) == shift.getStartTime().get(Calendar.YEAR)
                    && unavailability.get(Calendar.DAY_OF_YEAR)
                    == shift.getStartTime().get(Calendar.DAY_OF_YEAR)) {
                return false;
            }
        }
        for (Availability availability : mAvailabilities) {
            if (availability.doesCover(shift)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMinShifts() {
        return mRosteredShifts.size() >= mMinShifts;
    }

    public boolean hasMaxShifts() {
        return mRosteredShifts.size() >= mMaxShifts;
    }

    @Override
    public String toString() {
        return mName + "(" + mPossibleShifts.size() + " possible shifts, " + (mMinShifts - mRosteredShifts.size()) + " required shifts)";
    }
}
