package au.com.psilisoft.www.rosterbuilder;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.BaseAdapter;
import android.widget.SimpleCursorAdapter;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;


public class StaffListActivity extends SimpleListActivity {
    @Override
    protected String getMessage() {
        return "Staff List";
    }

    protected Cursor getCursor() {
        String[] projection = {Contract.Staff.ID, Contract.Staff.NAME};
        return getContentResolver().query(Contract.Staff.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, getCursor(),
                new String[]{Contract.Staff.NAME}, new int[]{android.R.id.text1}, 0);
    }

    @Override
    protected void updateAdapter(BaseAdapter adapter) {
        ((SimpleCursorAdapter) adapter).changeCursor(getCursor());
    }

    @Override
    protected void onActionNewClicked() {
        ContentValues values = new ContentValues();
        values.put(Contract.Staff.NAME, "New Staff");
        values.put(Contract.Staff.MIN_SHIFTS, 0);
        values.put(Contract.Staff.MAX_SHIFTS, 0);
        values.put(Contract.Staff.MIN_HOURS, 0);
        values.put(Contract.Staff.MAX_HOURS, 0);
        startStaffActivity(Long.valueOf(getContentResolver()
                .insert(Contract.Staff.CONTENT_URI, values).getLastPathSegment()));
    }

    @Override
    protected void onListItemClick(Object object) {
        startStaffActivity(((Cursor)object).getLong(((Cursor)object).getColumnIndex(Contract.Staff.ID)));
    }

    @Override
    protected void delete(long id) {
        getContentResolver().delete(Uri.withAppendedPath(Contract.Staff.CONTENT_URI,
                String.valueOf(id)), null, null);
    }

    private void startStaffActivity(long id) {
        Intent intent = new Intent(this, StaffActivity.class);
        intent.putExtra(StaffActivity.EXTRA_ID, id);
        startActivity(intent);
    }
}
