package au.com.psilisoft.www.rosterbuilder.custom_objects;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;

/**
 * Created by Fletcher on 17/08/2015.
 */
public class Availability implements TimedSegmentInterface {

    private long mId;
    private Calendar mStartTime;
    private Calendar mFinishTime;

    private SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");

    public Availability(Cursor c) {

        mId = c.getLong(c.getColumnIndex(Contract.StaffAvailability.ID));
        mStartTime = new GregorianCalendar();
        mStartTime.setTimeInMillis(c.getLong(c
                .getColumnIndex(Contract.StaffAvailability.START_TIME)));
        mFinishTime = new GregorianCalendar();
        mFinishTime.setTimeInMillis(c.getLong(c
                .getColumnIndex(Contract.StaffAvailability.FINISH_TIME)));

    }
    @Override
    public long getId() {
        return mId;
    }

    @Override
    public int getDay() {
        return mStartTime.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public Calendar getStartTime() {
        return mStartTime;
    }

    @Override
    public Calendar getFinishTime() {
        return mFinishTime;
    }

    @Override
    public String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        return format.format(mStartTime.getTime());
    }

    @Override
    public String getStartTimeString() {
        return mTimeFormat.format(mStartTime.getTime());
    }

    @Override
    public String getFinishTimeString() {
        return mTimeFormat.format(mFinishTime.getTime());
    }

    public boolean doesCover(Shift shift) {

        if (mStartTime.get(Calendar.DAY_OF_WEEK) != shift.getStartTime().get(Calendar.DAY_OF_WEEK)) {
            return false;
        }
        int start = mStartTime.get(Calendar.HOUR_OF_DAY) * 60 + mStartTime.get(Calendar.MINUTE);

        int shiftStart = shift.getStartTime().get(Calendar.HOUR_OF_DAY) * 60
                + shift.getStartTime().get(Calendar.MINUTE);

        int finish = mFinishTime.get(Calendar.HOUR_OF_DAY) * 60 + mFinishTime.get(Calendar.MINUTE);

        int shiftFinish = shift.getFinishTime().get(Calendar.HOUR_OF_DAY) * 60
                + shift.getFinishTime().get(Calendar.MINUTE);

        return (start <= shiftStart && finish >= shiftFinish);
    }


}
