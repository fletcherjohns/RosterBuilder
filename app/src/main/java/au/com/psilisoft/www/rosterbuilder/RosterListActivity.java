package au.com.psilisoft.www.rosterbuilder;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.BaseAdapter;
import android.widget.SimpleCursorAdapter;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;


public class RosterListActivity extends SimpleListActivity {

    @Override
    protected String getMessage() {
        return "Rosters";
    }

    protected Cursor getCursor() {
        return getContentResolver().query(Contract.Roster.CONTENT_URI, null, null, null, null);
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, getCursor(),
                new String[]{Contract.Roster.NAME, Contract.Roster.START_DATE},
                new int[]{android.R.id.text1, android.R.id.text2}, 0);
    }

    @Override
    protected void updateAdapter(BaseAdapter adapter) {
        ((SimpleCursorAdapter)adapter).changeCursor(getCursor());
    }

    @Override
    protected void onActionNewClicked() {
        startCreateRosterActivity();
    }

    @Override
    protected void onListItemClick(Object object) {
        startShiftListActivity(((Cursor) object).getLong(((Cursor) object)
                .getColumnIndex(Contract.Roster.ID)));
    }

    @Override
    protected void delete(long id) {

        // Delete roster as well as associated RosterSegments and RosterSegmentAbilities.

        // Delete the roster itself.
        getContentResolver().delete(Uri
                .withAppendedPath(Contract.Roster.CONTENT_URI, String.valueOf(id)),
                null, null);

        // Get a cursor of ids for all RosterSegments associated with the roster
        String[] columns = {Contract.RosterSegment.ID};
        String where = Contract.RosterSegment.ROSTER_ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor c = getContentResolver().query(Contract.RosterSegment.CONTENT_URI, columns, where,
                whereArgs, null);

        // For each RosterSegmentId, delete associated Abilities.
        if (c.moveToFirst()) {
            where = "";
            whereArgs = new String[c.getCount()];
            do {
                where = where.concat(Contract.RosterSegmentAbilities.ROSTER_SEGMENT_ID + "=?"
                        + (c.isLast() ? "" : " OR "));
                whereArgs[c.getPosition()] = String.valueOf(c.getLong(c
                        .getColumnIndex(Contract.RosterSegment.ID)));
            } while (c.moveToNext());
            getContentResolver().delete(Contract.RosterSegmentAbilities.CONTENT_URI,
                    where, whereArgs);
        }
        c.close();

        // Then delete all associated Shifts and Team Requirements
        where = Contract.RosterSegment.ROSTER_ID + "=?";
        whereArgs = new String[]{String.valueOf(id)};
        getContentResolver().delete(Contract.RosterSegment.CONTENT_URI, where, whereArgs);
    }

    private void startCreateRosterActivity() {
        Intent intent = new Intent(this, CreateRosterActivity.class);
        startActivity(intent);
    }

    private void startShiftListActivity(long id) {
        Intent intent = new Intent(this, ShiftListActivity.class);
        intent.putExtra(ShiftListActivity.EXTRA_ID, id);
        startActivity(intent);
    }
}
