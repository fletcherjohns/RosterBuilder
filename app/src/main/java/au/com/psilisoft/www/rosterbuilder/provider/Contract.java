package au.com.psilisoft.www.rosterbuilder.provider;

import android.net.Uri;


/**
 * Created by Fletcher on 11/06/2015.
 */
public class Contract {

    public static final String AUTHORITY = "au.com.psilisoft.www.rosterbuilder.roster_provider";
    public static final String URL = "content://" + AUTHORITY;

    public static final String DATABASE_NAME = "roster_database";
    public static final int DATABASE_VERSION = 1;

    /*
    All tables are defined as inner classes containing table name, column names and a
    create table statement
     */

    public static final class Ability {
        public static final String TABLE_NAME = "ability_table";
        public static final Uri CONTENT_URI = Uri.parse(URL + "/" + TABLE_NAME);

        public static final String ID = "_id";
        public static final String NAME = "name";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
                + ID + " integer primary key autoincrement, "
                + NAME + " text not null);";
    }

    public static final class Staff {
        public static final String TABLE_NAME = "staff_table";
        public static final Uri CONTENT_URI = Uri.parse(URL + "/" + TABLE_NAME);

        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String MIN_SHIFTS = "min_shifts";
        public static final String MAX_SHIFTS = "max_shifts";
        public static final String MIN_HOURS = "min_hours";
        public static final String MAX_HOURS = "max_hours";
        public static final String MIN_CONSEC_DAYS_OFF_PER_WEEK = "min_consec_days_off_per_week";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
                + ID + " integer primary key autoincrement, "
                + NAME + " text not null, "
                + MIN_SHIFTS + " integer not null, "
                + MAX_SHIFTS + " integer not null, "
                + MIN_HOURS + " integer not null, "
                + MAX_HOURS + " integer not null, "
                + MIN_CONSEC_DAYS_OFF_PER_WEEK + " integer not null);";
    }

    public static final class StaffAbilities {
        public static final String TABLE_NAME = "staff_abilities_table";
        public static final Uri CONTENT_URI = Uri.parse(URL + "/" + TABLE_NAME);

        public static final String ID = "_id";
        public static final String STAFF_ID = "staff_id";
        public static final String ABILITY_ID = "ability_id";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
                + ID + " integer primary key autoincrement, "
                + STAFF_ID + " integer not null, "
                + ABILITY_ID + " integer not null);";
    }

    public static final class StaffAvailability {
        public static final String TABLE_NAME = "staff_availability_table";
        public static final Uri CONTENT_URI = Uri.parse(URL + "/" + TABLE_NAME);

        public static final String ID = "_id";
        public static final String STAFF_ID = "staff_id";
        public static final String START_TIME = "start_time";
        public static final String FINISH_TIME = "finish_time";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
                + ID + " integer primary key autoincrement, "
                + STAFF_ID + " integer not null, "
                + START_TIME + " integer not null, "
                + FINISH_TIME + " integer not null);";
    }

    public static final class StaffUnavailability {
        public static final String TABLE_NAME = "staff_unavailability_table";
        public static final Uri CONTENT_URI = Uri.parse(URL + "/" + TABLE_NAME);

        public static final String ID = "_id";
        public static final String STAFF_ID = "staff_id";
        public static final String DATE = "date";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
                + ID + " integer primary key autoincrement, "
                + STAFF_ID + " integer not null, "
                + DATE + " integer not null);";
    }

    public static final class Roster {
        public static final String TABLE_NAME = "roster_table";
        public static final Uri CONTENT_URI = Uri.parse(URL + "/" + TABLE_NAME);

        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
                + ID + " integer primary key autoincrement, "
                + NAME + " text not null, "
                + START_DATE + " integer not null, "
                + END_DATE + " integer not null);";
    }

    public static final class RosterSegment {
        public static final String TABLE_NAME = "roster_segment_table";
        public static final Uri CONTENT_URI = Uri.parse(URL + "/" + TABLE_NAME);

        public static final String ID = "_id";
        public static final String ROSTER_ID = "roster_id";
        public static final String START_TIME = "start_time";
        public static final String FINISH_TIME = "finish_time";
        public static final String IS_SHIFT = "is_shift";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
                + ID + " integer primary key autoincrement, "
                + ROSTER_ID + " integer not null, "
                + START_TIME + " integer not null, "
                + FINISH_TIME + " integer not null, "
                + IS_SHIFT + " integer not null);";
    }

    public static final class RosterSegmentAbilities {
        public static final String TABLE_NAME = "roster_segment_abilities_table";
        public static final Uri CONTENT_URI = Uri.parse(URL + "/" + TABLE_NAME);

        public static final String ID = "_id";
        public static final String ROSTER_SEGMENT_ID = "shift_id";
        public static final String ABILITY_ID = "ability_id";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
                + ID + " integer primary key autoincrement, "
                + ROSTER_SEGMENT_ID + " integer not null, "
                + ABILITY_ID + " integer not null);";
    }

    public static final class RosterSegmentStaff {
        public static final String TABLE_NAME = "roster_segment_staff_table";
        public static final Uri CONTENT_URI = Uri.parse(URL + "/" + TABLE_NAME);

        public static final String ID = "_id";
        public static final String ROSTER_SEGMENT_ID = "roster_segment_id";
        public static final String STAFF_ID = "staff_id";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
                + ID + " integer primary key autoincrement, "
                + ROSTER_SEGMENT_ID + " integer not null, "
                + STAFF_ID + " integer not null);";
    }
}
