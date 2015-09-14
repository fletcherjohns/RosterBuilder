package au.com.psilisoft.www.rosterbuilder.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Fletcher on 12/06/2015.
 */
public class RosterProvider extends ContentProvider {

    /*
    int constants for UriMatcher
     */
    private static final int ABILITY_ID = 1;
    private static final int ABILITIES = 2;
    private static final int STAFF_ID = 3;
    private static final int STAFF = 4;
    private static final int STAFF_ABILITY_ID = 5;
    private static final int STAFF_ABILITIES = 6;
    private static final int STAFF_AVAILABILITY_ID = 7;
    private static final int STAFF_AVAILABILITIES = 8;
    private static final int STAFF_UNAVAILABILITY_ID = 9;
    private static final int STAFF_UNAVAILABILITIES = 10;
    private static final int ROSTER_ID = 11;
    private static final int ROSTERS = 12;
    private static final int ROSTER_SEGMENT_ID = 13;
    private static final int ROSTER_SEGMENTS = 14;
    private static final int ROSTER_SEGMENT_ABILITY_ID = 15;
    private static final int ROSTER_SEGMENT_ABILITIES = 16;
    private static final int ROSTER_SEGMENT_STAFF_ID = 17;
    private static final int ROSTER_SEGMENT_STAFF = 18;

    private static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);


        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.Ability.TABLE_NAME + "/#", ABILITY_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.Ability.TABLE_NAME, ABILITIES);

        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.Staff.TABLE_NAME + "/#", STAFF_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.Staff.TABLE_NAME, STAFF);

        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.StaffAbilities.TABLE_NAME + "/#", STAFF_ABILITY_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.StaffAbilities.TABLE_NAME, STAFF_ABILITIES);

        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.StaffAvailability.TABLE_NAME + "/#", STAFF_AVAILABILITY_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.StaffAvailability.TABLE_NAME, STAFF_AVAILABILITIES);

        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.StaffUnavailability.TABLE_NAME + "/#", STAFF_UNAVAILABILITY_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.StaffUnavailability.TABLE_NAME, STAFF_UNAVAILABILITIES);

        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.Roster.TABLE_NAME + "/#", ROSTER_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.Roster.TABLE_NAME, ROSTERS);

        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.RosterSegment.TABLE_NAME + "/#", ROSTER_SEGMENT_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.RosterSegment.TABLE_NAME, ROSTER_SEGMENTS);

        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.RosterSegmentAbilities.TABLE_NAME + "/#", ROSTER_SEGMENT_ABILITY_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.RosterSegmentAbilities.TABLE_NAME, ROSTER_SEGMENT_ABILITIES);


        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.RosterSegmentStaff.TABLE_NAME + "/#", ROSTER_SEGMENT_STAFF_ID);
        URI_MATCHER.addURI(Contract.AUTHORITY,
                Contract.RosterSegmentStaff.TABLE_NAME, ROSTER_SEGMENT_STAFF);
    }

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {

        mDbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        /*
        cases are arranged in pairs. The first of each pair intentionally falls
        through to the second because they use the same table.
         */

        switch (URI_MATCHER.match(uri)) {

            case ABILITY_ID:
                builder.appendWhere(Contract.Ability.ID + "=" + uri.getLastPathSegment());
            case ABILITIES:
                builder.setTables(Contract.Ability.TABLE_NAME);
                break;

            case STAFF_ID:
                builder.appendWhere(Contract.Staff.ID + "=" + uri.getLastPathSegment());
            case STAFF:
                builder.setTables(Contract.Staff.TABLE_NAME);
                break;

            case STAFF_ABILITY_ID:
                builder.appendWhere(Contract.StaffAbilities.ID + "=" + uri.getLastPathSegment());
            case STAFF_ABILITIES:
                projection = new String[]{
                        Contract.Ability.TABLE_NAME + "." + Contract.Ability.ID,
                        Contract.Ability.TABLE_NAME + "." + Contract.Ability.NAME
                };

                builder.setTables(Contract.StaffAbilities.TABLE_NAME
                        + " INNER JOIN " + Contract.Ability.TABLE_NAME
                        + " ON " + Contract.StaffAbilities.ABILITY_ID + "=" + Contract.Ability.ID);

                Map<String, String> mColumnMap = new HashMap<>();
                mColumnMap.put(projection[0], projection[0] + " AS " + Contract.Ability.ID);
                mColumnMap.put(projection[1], projection[1] + " AS " + Contract.Ability.NAME);
                builder.setProjectionMap(mColumnMap);
                break;

            case STAFF_AVAILABILITY_ID:
                builder.appendWhere(Contract.StaffAvailability.ID + "=" + uri.getLastPathSegment());
            case STAFF_AVAILABILITIES:
                builder.setTables(Contract.StaffAvailability.TABLE_NAME);
                break;

            case STAFF_UNAVAILABILITY_ID:
                builder.appendWhere(Contract.StaffUnavailability.ID + "=" + uri.getLastPathSegment());
            case STAFF_UNAVAILABILITIES:
                builder.setTables(Contract.StaffUnavailability.TABLE_NAME);
                break;

            case ROSTER_ID:
                builder.appendWhere(Contract.Roster.ID + "=" + uri.getLastPathSegment());
            case ROSTERS:
                builder.setTables(Contract.Roster.TABLE_NAME);
                break;

            case ROSTER_SEGMENT_ID:
                builder.appendWhere(Contract.RosterSegment.ID + "=" + uri.getLastPathSegment());
            case ROSTER_SEGMENTS:
                builder.setTables(Contract.RosterSegment.TABLE_NAME);
                break;

            case ROSTER_SEGMENT_ABILITY_ID:
                builder.appendWhere(Contract.RosterSegmentAbilities.ID + "=" + uri.getLastPathSegment());
            case ROSTER_SEGMENT_ABILITIES:
                builder.setTables(Contract.RosterSegmentAbilities.TABLE_NAME);
                break;

            case ROSTER_SEGMENT_STAFF_ID:
                builder.appendWhere(Contract.RosterSegmentStaff.ID + "=" + uri.getLastPathSegment());
            case ROSTER_SEGMENT_STAFF:
                builder.setTables(Contract.RosterSegmentStaff.TABLE_NAME);
                break;

            default:
                throw new IllegalArgumentException("Invalid uri for StaffProvider.query()");
        }

        mDb = mDbHelper.getReadableDatabase();
        Cursor c = builder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String table;
        switch (URI_MATCHER.match(uri)) {

            case ABILITIES:
                table = Contract.Ability.TABLE_NAME;
                break;
            case STAFF:
                table = Contract.Staff.TABLE_NAME;
                break;
            case STAFF_ABILITIES:
                table = Contract.StaffAbilities.TABLE_NAME;
                break;
            case STAFF_AVAILABILITIES:
                table = Contract.StaffAvailability.TABLE_NAME;
                break;
            case STAFF_UNAVAILABILITIES:
                table = Contract.StaffUnavailability.TABLE_NAME;
                break;
            case ROSTERS:
                table = Contract.Roster.TABLE_NAME;
                break;
            case ROSTER_SEGMENTS:
                table = Contract.RosterSegment.TABLE_NAME;
                break;
            case ROSTER_SEGMENT_ABILITIES:
                table = Contract.RosterSegmentAbilities.TABLE_NAME;
                break;
            case ROSTER_SEGMENT_STAFF:
                table = Contract.RosterSegmentStaff.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Invalid uri for StaffProvider.insert()");
        }

        mDb = mDbHelper.getWritableDatabase();
        try {
            return Uri.withAppendedPath(uri, String.valueOf(mDb.insert(table, null, values)));
        } finally {
            mDb.close();
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table;
        switch (URI_MATCHER.match(uri)) {

            case ABILITY_ID:
                selection = Contract.Ability.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case ABILITIES:
                table = Contract.Ability.TABLE_NAME;
                break;

            case STAFF_ID:
                selection = Contract.Staff.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case STAFF:
                table = Contract.Staff.TABLE_NAME;
                break;

            case STAFF_ABILITY_ID:
                selection = Contract.StaffAbilities.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case STAFF_ABILITIES:
                table = Contract.StaffAbilities.TABLE_NAME;
                break;

            case STAFF_AVAILABILITY_ID:
                selection = Contract.StaffAvailability.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case STAFF_AVAILABILITIES:
                table = Contract.StaffAvailability.TABLE_NAME;
                break;

            case STAFF_UNAVAILABILITY_ID:
                selection = Contract.StaffUnavailability.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case STAFF_UNAVAILABILITIES:
                table = Contract.StaffUnavailability.TABLE_NAME;
                break;

            case ROSTER_ID:
                selection = Contract.Roster.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case ROSTERS:
                table = Contract.Roster.TABLE_NAME;
                break;

            case ROSTER_SEGMENT_ID:
                selection = Contract.RosterSegment.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case ROSTER_SEGMENTS:
                table = Contract.RosterSegment.TABLE_NAME;
                break;

            case ROSTER_SEGMENT_ABILITY_ID:
                selection = Contract.RosterSegmentAbilities.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case ROSTER_SEGMENT_ABILITIES:
                table = Contract.RosterSegmentAbilities.TABLE_NAME;
                break;

            case ROSTER_SEGMENT_STAFF_ID:
                selection = Contract.RosterSegmentStaff.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case ROSTER_SEGMENT_STAFF:
                table = Contract.RosterSegmentStaff.TABLE_NAME;
                break;

            default:
                throw new IllegalArgumentException("Invalid uri for StaffProvider.delete()");
        }
        mDb = mDbHelper.getWritableDatabase();
        try {
            return mDb.delete(table, selection, selectionArgs);
        } finally {
            mDb.close();
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table;
        switch (URI_MATCHER.match(uri)) {

            case ABILITY_ID:
                selection = Contract.Ability.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case ABILITIES:
                table = Contract.Ability.TABLE_NAME;
                break;

            case STAFF_ID:
                selection = Contract.Staff.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case STAFF:
                table = Contract.Staff.TABLE_NAME;
                break;

            case STAFF_ABILITY_ID:
                selection = Contract.StaffAbilities.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case STAFF_ABILITIES:
                table = Contract.StaffAbilities.TABLE_NAME;
                break;

            case STAFF_AVAILABILITY_ID:
                selection = Contract.StaffAvailability.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case STAFF_AVAILABILITIES:
                table = Contract.StaffAvailability.TABLE_NAME;
                break;

            case STAFF_UNAVAILABILITY_ID:
                selection = Contract.StaffUnavailability.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case STAFF_UNAVAILABILITIES:
                table = Contract.StaffUnavailability.TABLE_NAME;
                break;

            case ROSTER_ID:
                selection = Contract.Roster.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case ROSTERS:
                table = Contract.Roster.TABLE_NAME;
                break;

            case ROSTER_SEGMENT_ID:
                selection = Contract.RosterSegment.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case ROSTER_SEGMENTS:
                table = Contract.RosterSegment.TABLE_NAME;
                break;

            case ROSTER_SEGMENT_ABILITY_ID:
                selection = Contract.RosterSegmentAbilities.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case ROSTER_SEGMENT_ABILITIES:
                table = Contract.RosterSegmentAbilities.TABLE_NAME;
                break;

            case ROSTER_SEGMENT_STAFF_ID:
                selection = Contract.RosterSegmentStaff.ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
            case ROSTER_SEGMENT_STAFF:
                table = Contract.RosterSegmentStaff.TABLE_NAME;
                break;

            default:
                throw new IllegalArgumentException("Invalid uri for StaffProvider.update()");
        }
        mDb = mDbHelper.getWritableDatabase();
        try {
            return mDb.update(table, values, selection, selectionArgs);
        } finally {
            mDb.close();
        }
    }



    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, Contract.DATABASE_NAME, null, Contract.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Contract.Ability.CREATE_TABLE);
            db.execSQL(Contract.Staff.CREATE_TABLE);
            db.execSQL(Contract.StaffAbilities.CREATE_TABLE);
            db.execSQL(Contract.StaffAvailability.CREATE_TABLE);
            db.execSQL(Contract.StaffUnavailability.CREATE_TABLE);
            db.execSQL(Contract.Roster.CREATE_TABLE);
            db.execSQL(Contract.RosterSegment.CREATE_TABLE);
            db.execSQL(Contract.RosterSegmentAbilities.CREATE_TABLE);
            db.execSQL(Contract.RosterSegmentStaff.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
