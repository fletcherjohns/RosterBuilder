package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;


public class RosterSegmentActivity extends Activity {

    public static final String EXTRA_ID = "extra_id";
    private static final int MINIMUM_SHIFT_LENGTH = 3 * 60;

    private int mMaxStartTime;
    private int mMinFinishTime;

    private ContentValues mValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roster_segment);

        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        Cursor shift = getContentResolver().query(Uri.withAppendedPath(Contract.RosterSegment.CONTENT_URI, String.valueOf(id)),
                null, null, null, null);
        if (!shift.moveToFirst()) finish();

        mValues = new ContentValues();
        final Calendar startTime = new GregorianCalendar();
        startTime.setTimeInMillis(shift.getLong(shift
                .getColumnIndex(Contract.RosterSegment.START_TIME)));
        final Calendar finishTime = new GregorianCalendar();
        finishTime.setTimeInMillis(shift.getLong(shift
                .getColumnIndex(Contract.RosterSegment.FINISH_TIME)));

        final TextView textDay = (TextView) findViewById(R.id.text_day);
        textDay.setText(DateFormat.getDateFormat(this).format(startTime.getTime()));

        final CheckBox checkBoxIsShift = (CheckBox) findViewById(R.id.check_box_is_shift);
        checkBoxIsShift.setChecked(shift.getInt(shift
                .getColumnIndex(Contract.RosterSegment.IS_SHIFT)) == 1);
        shift.close();

        checkBoxIsShift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mValues.put(Contract.RosterSegment.IS_SHIFT, isChecked ? 1 : 0);
                update();
            }
        });

        final TimePicker pickerStartTime = (TimePicker) findViewById(R.id.picker_start_time);
        pickerStartTime.setCurrentHour(startTime.get(Calendar.HOUR_OF_DAY));
        pickerStartTime.setCurrentMinute(startTime.get(Calendar.MINUTE));
        mMinFinishTime = (pickerStartTime.getCurrentHour() * 60
                + pickerStartTime.getCurrentMinute()) + MINIMUM_SHIFT_LENGTH;
        pickerStartTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                if (hourOfDay * 60 + minute > mMaxStartTime) {
                    pickerStartTime.setCurrentHour(hourOfDay = mMaxStartTime / 60);
                    pickerStartTime.setCurrentMinute(minute = mMaxStartTime % 60);
                    return;
                }
                mMinFinishTime = hourOfDay * 60 + minute + MINIMUM_SHIFT_LENGTH;
                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTime.set(Calendar.MINUTE, minute);
                mValues.put(Contract.RosterSegment.START_TIME, startTime.getTimeInMillis());
                update();
            }
        });
        final TimePicker pickerFinishTime = (TimePicker) findViewById(R.id.picker_finish_time);
        pickerFinishTime.setCurrentHour(finishTime.get(Calendar.HOUR_OF_DAY));
        pickerFinishTime.setCurrentMinute(finishTime.get(Calendar.MINUTE));
        mMaxStartTime = (pickerFinishTime.getCurrentHour() * 60
                + pickerFinishTime.getCurrentMinute()) - MINIMUM_SHIFT_LENGTH;
        pickerFinishTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                if (hourOfDay * 60 + minute < mMinFinishTime) {
                    pickerFinishTime.setCurrentHour(mMinFinishTime / 60);
                    pickerFinishTime.setCurrentMinute(mMinFinishTime % 60);
                    return;
                }
                mMaxStartTime = hourOfDay * 60 + minute - MINIMUM_SHIFT_LENGTH;
                finishTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                finishTime.set(Calendar.MINUTE, minute);
                mValues.put(Contract.RosterSegment.FINISH_TIME, finishTime.getTimeInMillis());
                update();
            }
        });
        final Button abilitiesButton = (Button) findViewById(R.id.button_shift_abilities);
        abilitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startObjectAbilitiesActivity();
            }
        });
    }

    private void startObjectAbilitiesActivity() {

        Intent intent = new Intent(this, ObjectAbilitiesActivity.class);
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_ID, getIntent().getLongExtra(EXTRA_ID, -1));
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_LINK_CONTENT_URI,
                Contract.RosterSegmentAbilities.CONTENT_URI);
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_ABILITY_CONTENT_URI,
                Contract.Ability.CONTENT_URI);
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_COLUMN_LINK_TABLE_OBJECT_ID,
                Contract.RosterSegmentAbilities.ROSTER_SEGMENT_ID);
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_COLUMN_LINK_TABLE_ABILITY_ID,
                Contract.RosterSegmentAbilities.ABILITY_ID);
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_COLUMN_ABILITY_TABLE_NAME,
                Contract.Ability.NAME);
        startActivity(intent);
    }

    private void update() {

        getContentResolver().update(Uri.withAppendedPath(Contract.RosterSegment.CONTENT_URI,
                        String.valueOf(getIntent().getLongExtra(EXTRA_ID, -1))),
                mValues, null, null);
        mValues.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shift, menu);
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
