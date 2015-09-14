package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;

public class CreateRosterSegmentActivity extends Activity {

    public static final String EXTRA_ID = "extra_id";
    private static final int MINIMUM_SHIFT_LENGTH = 3 * 60;

    private long mRosterId;
    private List<Date> mDateList;
    private SimpleCursorAdapter mAdapter;
    private ListView mListViewAbilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roster_segment);

        mDateList = new ArrayList<>();
        mRosterId = getIntent().getLongExtra(EXTRA_ID, -1);

        findViewById(R.id.text_day_label).setVisibility(View.GONE);
        findViewById(R.id.text_day).setVisibility(View.GONE);
        findViewById(R.id.button_shift_abilities).setVisibility(View.GONE);


        final Button buttonSelectDays = (Button) findViewById(R.id.button_select_days);
        buttonSelectDays.setVisibility(View.VISIBLE);
        buttonSelectDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectDayDialog();
            }
        });

        mListViewAbilities = (ListView) findViewById(R.id.list_view_abilities);
        mListViewAbilities.setVisibility(View.VISIBLE);
        mListViewAbilities.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        final Cursor c = getContentResolver().query(Contract.Ability.CONTENT_URI,
                null, null, null, null);

        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_checked, c,
                new String[] {Contract.Ability.NAME}, new int[]{android.R.id.text1}, 0);
        mListViewAbilities.setAdapter(mAdapter);

        final Button buttonCreate = (Button) findViewById(R.id.button_create);
        buttonCreate.setVisibility(View.VISIBLE);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createRosterSegments();
                finish();
            }
        });
    }

    private void createRosterSegments() {

        TimePicker pickerStartTime = (TimePicker) findViewById(R.id.picker_start_time);

        TimePicker pickerFinishTime = (TimePicker) findViewById(R.id.picker_finish_time);

        CheckBox checkBoxIsShift = (CheckBox) findViewById(R.id.check_box_is_shift);

        if (checkBoxIsShift.isChecked() &&
                (pickerFinishTime.getCurrentHour() * 60 + pickerFinishTime.getCurrentMinute())
                - (pickerStartTime.getCurrentHour() * 60 + pickerStartTime.getCurrentMinute())
                < MINIMUM_SHIFT_LENGTH) {
            Toast.makeText(this, "Invalid times", Toast.LENGTH_SHORT).show();
            return;
        }

        long[] abilityIds = mListViewAbilities.getCheckedItemIds();

        ContentValues values = new ContentValues();
        values.put(Contract.RosterSegment.ROSTER_ID, mRosterId);

        for (Date date : mDateList) {

            Calendar startTime = new GregorianCalendar();
            startTime.setTime(date);
            startTime.set(Calendar.HOUR_OF_DAY, pickerStartTime.getCurrentHour());
            startTime.set(Calendar.MINUTE, pickerStartTime.getCurrentMinute());

            Calendar finishTime = new GregorianCalendar();
            finishTime.setTime(date);
            finishTime.set(Calendar.HOUR_OF_DAY, pickerFinishTime.getCurrentHour());
            finishTime.set(Calendar.MINUTE, pickerFinishTime.getCurrentMinute());

            values.put(Contract.RosterSegment.IS_SHIFT, checkBoxIsShift.isChecked() ? 1 : 0);
            values.put(Contract.RosterSegment.START_TIME, startTime.getTimeInMillis());
            values.put(Contract.RosterSegment.FINISH_TIME, finishTime.getTimeInMillis());

            long rosterSegmentId = Long.parseLong(getContentResolver()
                    .insert(Contract.RosterSegment.CONTENT_URI, values)
                    .getLastPathSegment());

            ContentValues abilityValues = new ContentValues();
            abilityValues.put(Contract.RosterSegmentAbilities
                    .ROSTER_SEGMENT_ID, rosterSegmentId);

            for (long abilityId : abilityIds) {

                abilityValues.put(Contract.RosterSegmentAbilities
                        .ABILITY_ID, abilityId);

                getContentResolver().insert(Contract.RosterSegmentAbilities
                        .CONTENT_URI, abilityValues);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.changeCursor(null);
    }

    private void startSelectDayDialog() {

        // Get a cursor containing the start and end dates for roster_table/mRosterId
        String[] projection = {Contract.Roster.START_DATE, Contract.Roster.END_DATE};
        Cursor c = getContentResolver().query(Uri.withAppendedPath(Contract.Roster.CONTENT_URI, String.valueOf(mRosterId)),
                projection, null, null, null);
        if (!c.moveToFirst()) return;
        // Create GregorianCalendar set to start_date
        Calendar startDate = new GregorianCalendar();
        startDate.setTimeInMillis(c.getLong(c.getColumnIndex(Contract.Roster.START_DATE)));
        // Create GregorianCalendar set to end_date + 1 day (for CalendarPickerView)
        Calendar endDate = new GregorianCalendar();
        endDate.setTimeInMillis(c.getLong(c.getColumnIndex(Contract.Roster.END_DATE)));
        endDate.roll(Calendar.DAY_OF_YEAR, 1);
        // Close the cursor
        c.close();
        // Create CalendarPickerView for use in AlertDialog
        final CalendarPickerView v = (CalendarPickerView) getLayoutInflater()
                .inflate(R.layout.dialog_calendar, null, false);
        // Init v using startDate and endDate
        v.init(startDate.getTime(), endDate.getTime())
                // multiple dates selectable
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                // apply mDateList
                .withSelectedDates(mDateList);
        // Create AlertDialog.builder and setView(v)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        // On positive button press, simply replace mDateList with selectedDates from v
        builder.setPositiveButton("Select Dates", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDateList = v.getSelectedDates();
            }
        }).create().show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_roster_segment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
