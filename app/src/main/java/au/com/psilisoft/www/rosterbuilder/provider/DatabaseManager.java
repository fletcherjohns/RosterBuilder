package au.com.psilisoft.www.rosterbuilder.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import au.com.psilisoft.www.rosterbuilder.custom_objects.Availability;
import au.com.psilisoft.www.rosterbuilder.custom_objects.Shift;
import au.com.psilisoft.www.rosterbuilder.custom_objects.TeamRequirement;
import au.com.psilisoft.www.rosterbuilder.custom_objects.TimedSegment;

/**
 * Created by Fletcher on 21/08/2015.
 */
public class DatabaseManager {

    public static List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> getAbilities(Context context) {
        List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> list = new ArrayList<>();

        Cursor c = context.getContentResolver().query(Contract.Ability.CONTENT_URI,
                null, null, null, null);
        if (c.moveToFirst()) {
            do {
                list.add(new au.com.psilisoft.www.rosterbuilder.custom_objects.Ability(c));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public static List<au.com.psilisoft.www.rosterbuilder.custom_objects.Staff> getStaff(Context context) {
        return getStaff(context, getAbilities(context));
    }

    public static List<au.com.psilisoft.www.rosterbuilder.custom_objects.Staff> getStaff(Context context, List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> allAbilities) {

        List<au.com.psilisoft.www.rosterbuilder.custom_objects.Staff> list = new ArrayList<>();

        Cursor c = context.getContentResolver().query(Contract.Staff.CONTENT_URI,
                null, null, null, null);

        if (c.moveToFirst()) {
            do {
                au.com.psilisoft.www.rosterbuilder.custom_objects.Staff staff = new au.com.psilisoft.www.rosterbuilder.custom_objects.Staff(c);
                staff.setAvailabilities(getStaffAvailabilities(context, staff.getId()));
                staff.setUnavailabilities(getStaffUnavailabilities(context, staff.getId()));
                staff.setAbilities(getStaffAbilities(context, staff.getId(), allAbilities));
                list.add(staff);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public static au.com.psilisoft.www.rosterbuilder.custom_objects.Staff getStaff(Context context, long id) {

        au.com.psilisoft.www.rosterbuilder.custom_objects.Staff staff;

        Cursor c = context.getContentResolver().query(Uri.withAppendedPath(Contract.Staff.CONTENT_URI, String.valueOf(id)), null, null, null, null);
        if (c.moveToFirst()) {
            staff = new au.com.psilisoft.www.rosterbuilder.custom_objects.Staff(c);
        } else {
            staff = null;
        }
        c.close();
        return staff;
    }

    private static List<Availability> getStaffAvailabilities(Context context, long staffId) {
        List<Availability> list = new ArrayList<>();

        String where = Contract.StaffAvailability.STAFF_ID + "=?";
        String[] whereArgs = {String.valueOf(staffId)};

        Cursor c = context.getContentResolver().query(Contract.StaffAvailability.CONTENT_URI,
                null, where, whereArgs, null);

        if (c.moveToFirst()) {
            do {
                list.add(new Availability(c));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    private static List<Calendar> getStaffUnavailabilities(Context context, long staffId) {
        List<Calendar> list = new ArrayList<>();

        String[] projection = {Contract.StaffUnavailability.DATE};
        String where = Contract.StaffUnavailability.STAFF_ID + "=?";
        String[] whereArgs = {String.valueOf(staffId)};

        Cursor c = context.getContentResolver().query(Contract.StaffUnavailability.CONTENT_URI,
                projection, where, whereArgs, null);

        if (c.moveToFirst()) {
            do {
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(c.getLong(c
                        .getColumnIndex(Contract.StaffUnavailability.DATE)));

                list.add(cal);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    private static List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> getStaffAbilities(Context context, long staffId,
                                                   List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> allAbilities) {
        List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> list = new ArrayList<>();

        String[] projection = {Contract.StaffAbilities.ABILITY_ID};
        String where = Contract.StaffAbilities.STAFF_ID + "=?";
        String[] whereArgs = {String.valueOf(staffId)};

        Cursor c = context.getContentResolver().query(Contract.StaffAbilities.CONTENT_URI,
                projection, where, whereArgs, null);

        if (c.moveToFirst()) {
            do {
                for (au.com.psilisoft.www.rosterbuilder.custom_objects.Ability ability : allAbilities) {
                    if (ability.getId() == c.getLong(c
                            .getColumnIndex(Contract.StaffAbilities.ABILITY_ID))) {

                        list.add(ability);
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public static List<Shift> getRosterShifts(Context context, long rosterId) {
        return getRosterShifts(context, rosterId, getAbilities(context));
    }

    public static List<Shift> getRosterShifts(Context context, long rosterId,
                                              List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> allAbilities) {
        List<Shift> list = new ArrayList<>();

        String where = Contract.RosterSegment.ROSTER_ID + "=? AND "
                + Contract.RosterSegment.IS_SHIFT + "=?";
        String[] whereArgs = {String.valueOf(rosterId), String.valueOf(1)};

        Cursor c = context.getContentResolver().query(Contract.RosterSegment.CONTENT_URI,
                null, where, whereArgs, null);

        if (c.moveToFirst()) {
            do {
                Shift shift = new Shift(c);
                shift.setAbilities(getRosterSegmentAbilities(context, shift.getId(), allAbilities));
                list.add(new Shift(c));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public static List<TeamRequirement> getRosterTeamRequirements(Context context, long rosterId) {
        return getRosterTeamRequirements(context, rosterId, getAbilities(context));
    }

    public static List<TeamRequirement> getRosterTeamRequirements(Context context, long rosterId,
                                                                  List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> allAbilities) {
        List<TeamRequirement> list = new ArrayList<>();

        String where = Contract.RosterSegment.ROSTER_ID + "=? AND "
                + Contract.RosterSegment.IS_SHIFT + "=?";
        String[] whereArgs = {String.valueOf(rosterId), String.valueOf(0)};

        Cursor c = context.getContentResolver().query(Contract.RosterSegment.CONTENT_URI,
                null, where, whereArgs, null);

        if (c.moveToFirst()) {
            do {
                TeamRequirement teamRequirement = new TeamRequirement(c);
                teamRequirement.setAbilities(getRosterSegmentAbilities(context,
                        teamRequirement.getId(), allAbilities));
                list.add(teamRequirement);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public static List<TimedSegment> getRosterSegments(Context context, long rosterId) {
        return getRosterSegments(context, rosterId, getAbilities(context));
    }

    public static List<TimedSegment> getRosterSegments(Context context, long rosterId,
                                                       List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> allAbilities) {
        List<TimedSegment> list = new ArrayList<>();

        String where = Contract.RosterSegment.ROSTER_ID + "=?";
        String[] whereArgs = {String.valueOf(rosterId)};

        Cursor c = context.getContentResolver().query(Contract.RosterSegment.CONTENT_URI,
                null, where, whereArgs, null);
        if (c.moveToFirst()) {
            do {
                TimedSegment segment = TimedSegment.fromCursor(c);
                if (segment == null) continue;

                segment.setAbilities(getRosterSegmentAbilities(context, rosterId, allAbilities));
                list.add(segment);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public static List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> getRosterSegmentAbilities(Context context, long rosterSegmentId,
                                                           List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> allAbilities) {
        List<au.com.psilisoft.www.rosterbuilder.custom_objects.Ability> list = new ArrayList<>();

        String[] projection = {Contract.RosterSegmentAbilities.ABILITY_ID};
        String where = Contract.RosterSegmentAbilities.ROSTER_SEGMENT_ID + "=?";
        String[] whereArgs = {String.valueOf(rosterSegmentId)};

        Cursor c = context.getContentResolver().query(Contract.RosterSegmentAbilities.CONTENT_URI,
                projection, where, whereArgs, null);

        if (c.moveToFirst()) {
            do {
                for (au.com.psilisoft.www.rosterbuilder.custom_objects.Ability ability : allAbilities) {
                    if (ability.getId() == c.getLong(c
                            .getColumnIndex(Contract.RosterSegmentAbilities.ABILITY_ID))) {
                        list.add(ability);
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public static au.com.psilisoft.www.rosterbuilder.custom_objects.Staff getShiftStaff(Context context, long shiftId, List<au.com.psilisoft.www.rosterbuilder.custom_objects.Staff> allStaff) {


        String where = Contract.RosterSegmentStaff.ROSTER_SEGMENT_ID + "=?";
        String[] whereArgs = {String.valueOf(shiftId)};

        Cursor c = context.getContentResolver().query(Contract.RosterSegmentStaff.CONTENT_URI,
                null, where, whereArgs, null);

        if (c.moveToFirst()) {
            for (au.com.psilisoft.www.rosterbuilder.custom_objects.Staff staff : allStaff) {
                if (staff.getId() == c.getLong(c
                        .getColumnIndex(Contract.RosterSegmentStaff.STAFF_ID))) {
                    c.close();
                    return staff;
                }
            }
            c.close();
        }
        c.close();
        return null;
    }
}
