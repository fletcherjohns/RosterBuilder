package au.com.psilisoft.www.rosterbuilder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;

import java.util.List;

import au.com.psilisoft.www.rosterbuilder.custom_objects.Roster;
import au.com.psilisoft.www.rosterbuilder.custom_objects.TimedSegment;
import au.com.psilisoft.www.rosterbuilder.provider.Contract;


public class ShiftListActivity extends SimpleListActivity implements Roster.RosterInterface {

    public static final String EXTRA_ID = "extra_id";

    private Roster mRoster;
    private Thread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRoster = new Roster(this, getIntent().getLongExtra(EXTRA_ID, -1));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mThread = new Thread() {
            @Override
            public void run() {
                mRoster.fillShifts();
            }
        };
        mThread.start();
    }

    @Override
    protected String getMessage() {
        return "Shifts and Team Requirements";
    }

    protected List<TimedSegment> getList() {

        return mRoster.getRosterSegments();
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new RosterSegmentAdapter(this, getList());
    }

    @Override
    protected void updateAdapter(BaseAdapter adapter) {
        if (mRoster.updateRosterSegments()) {
            ((RosterSegmentAdapter) adapter).setList(getList());
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onActionNewClicked() {

        startCreateRosterSegmentActivity();
    }

    private void startRosterSegmentActivity(long id) {
        Intent intent = new Intent(this, RosterSegmentActivity.class);
        intent.putExtra(RosterSegmentActivity.EXTRA_ID, id);
        startActivity(intent);
    }

    private void startCreateRosterSegmentActivity() {
        Intent intent = new Intent(this, CreateRosterSegmentActivity.class);
        intent.putExtra(CreateRosterSegmentActivity.EXTRA_ID,
                getIntent().getLongExtra(EXTRA_ID, -1));
        startActivity(intent);
    }

    @Override
    protected void onListItemClick(Object object) {
        TimedSegment segment = (TimedSegment) object;
        startRosterSegmentActivity(segment.getId());
    }

    @Override
    protected void delete(long id) {

        getContentResolver().delete(Uri.withAppendedPath(Contract.RosterSegment.CONTENT_URI,
                String.valueOf(id)), null, null);

        String where = Contract.RosterSegmentAbilities.ROSTER_SEGMENT_ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        getContentResolver().delete(Contract.RosterSegmentAbilities.CONTENT_URI,
                where, whereArgs);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {


        if (v.getId() == R.id.list_view) {
            menu.add("Edit");
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Edit") {

            return true;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void notifyDataSetChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateAdapter();
            }
        });
    }
}
