package au.com.psilisoft.www.rosterbuilder.custom_objects;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;

/**
 * Created by Fletcher on 24/07/2015.
 */
public abstract class TimedSegment implements TimedSegmentInterface {

    private long mId;
    private Calendar mStartTime;
    private Calendar mFinishTime;
    private Set<Ability> mAbilities;

    private SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");

    public TimedSegment(Cursor main) {
        this(main, null);
    }

    public TimedSegment(Cursor main, Cursor abilities) {
        mId = main.getLong(main.getColumnIndex(Contract.RosterSegment.ID));
        mStartTime = new GregorianCalendar();
        mStartTime.setTimeInMillis(main.getLong(main
                .getColumnIndex(Contract.RosterSegment.START_TIME)));
        mFinishTime = new GregorianCalendar();
        mFinishTime.setTimeInMillis(main.getLong(main
                .getColumnIndex(Contract.RosterSegment.FINISH_TIME)));
        mAbilities = new HashSet<>();
        if (abilities != null && abilities.moveToFirst()) {
            do {
                mAbilities.add(new Ability(
                        abilities.getLong(abilities.getColumnIndex(Contract.Ability.ID)),
                        abilities.getString(abilities.getColumnIndex(Contract.Ability.NAME))
                ));

            } while (abilities.moveToNext());
        }
    }

    public void setAbilities(Collection<Ability> abilities) {
        mAbilities = new HashSet<>(abilities);
    }

    public void addAbility(Ability ability) {
        mAbilities.add(ability);
    }

    public void addAbilities(Collection<Ability> abilities) {
        mAbilities.addAll(abilities);
    }

    public void removeAbility(Ability ability) {
        mAbilities.remove(ability);
    }

    public static TimedSegment fromCursor(Cursor main, Cursor abilities) {

        switch (main.getInt(main.getColumnIndex(Contract.RosterSegment.IS_SHIFT))) {
            case 0:
                return new TeamRequirement(main, abilities);
            case 1:
                return new Shift(main, abilities);
            default:
                return null;
        }
    }

    public static TimedSegment fromCursor(Cursor main) {

        switch (main.getInt(main.getColumnIndex(Contract.RosterSegment.IS_SHIFT))) {
            case 0:
                return new TeamRequirement(main);
            case 1:
                return new Shift(main);
            default:
                return null;
        }
    }

    public long getId() {
        return mId;
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    public Calendar getFinishTime() {
        return mFinishTime;
    }

    public abstract boolean isShift();

    public Set<Ability> getAbilities() {
        return mAbilities;
    }

    public int getDay() {
        return mStartTime.get(Calendar.DAY_OF_YEAR);
    }

    public String getStartTimeString() {
        return mTimeFormat.format(mStartTime.getTime());
    }

    public String getFinishTimeString() {
        return mTimeFormat.format(mFinishTime.getTime());
    }

    public String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE dd/MM/yy");
        return format.format(mStartTime.getTime());
    }

    public String getDurationString() {
        long minutes = (mFinishTime.getTimeInMillis() - mStartTime.getTimeInMillis()) / 1000 / 60;

        return minutes / 60 + "h" + minutes % 60 + "m";
    }

    public boolean doesClash(Shift shift) {

        if (shift.getFinishTime().equals(mStartTime) || shift.getStartTime().equals(mFinishTime)) return false;

        Calendar lowerBuffer = new GregorianCalendar();
        lowerBuffer.setTimeInMillis(mStartTime.getTimeInMillis());
        lowerBuffer.add(Calendar.HOUR_OF_DAY, -10);
        Calendar upperBuffer = new GregorianCalendar();
        upperBuffer.setTimeInMillis(mFinishTime.getTimeInMillis());
        upperBuffer.add(Calendar.HOUR_OF_DAY, 10);


        if (shift.getStartTime().compareTo(mStartTime) < 0) {

            return shift.getFinishTime().compareTo(lowerBuffer) >= 0;
        } else {
            return shift.getStartTime().compareTo(upperBuffer) < 0;
        }
    }

    public boolean existsAtTime(long timeMillis) {

        return (mStartTime.getTimeInMillis() <= timeMillis
                && timeMillis < mFinishTime.getTimeInMillis());
    }
}
