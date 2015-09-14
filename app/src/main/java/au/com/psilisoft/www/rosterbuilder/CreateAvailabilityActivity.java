package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;

public class CreateAvailabilityActivity extends Activity {
    public static final String EXTRA_ID = "extra_id";
    private static final int MINIMUM_SHIFT_LENGTH = 3 * 60;

    private long mRosterId;
    private List<Date> mDateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roster_segment);

        mDateList = new ArrayList<>();
        mRosterId = getIntent().getLongExtra(EXTRA_ID, -1);

        findViewById(R.id.text_day_label).setVisibility(View.GONE);
        findViewById(R.id.check_box_is_shift).setVisibility(View.GONE);
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


        final Button buttonCreate = (Button) findViewById(R.id.button_create);
        buttonCreate.setVisibility(View.VISIBLE);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAvailabilities();
                finish();
            }
        });
    }

    private void createAvailabilities() {

        TimePicker pickerStartTime = (TimePicker) findViewById(R.id.picker_start_time);

        TimePicker pickerFinishTime = (TimePicker) findViewById(R.id.picker_finish_time);


        if ((pickerFinishTime.getCurrentHour() * 60 + pickerFinishTime.getCurrentMinute())
                - (pickerStartTime.getCurrentHour() * 60 + pickerStartTime.getCurrentMinute())
                < MINIMUM_SHIFT_LENGTH) {
            Toast.makeText(this, "Invalid times", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(Contract.StaffAvailability.STAFF_ID, mRosterId);

        for (Date date : mDateList) {

            Calendar startTime = new GregorianCalendar();
            startTime.setTime(date);
            startTime.set(Calendar.HOUR_OF_DAY, pickerStartTime.getCurrentHour());
            startTime.set(Calendar.MINUTE, pickerStartTime.getCurrentMinute());

            Calendar finishTime = new GregorianCalendar();
            finishTime.setTime(date);
            finishTime.set(Calendar.HOUR_OF_DAY, pickerFinishTime.getCurrentHour());
            finishTime.set(Calendar.MINUTE, pickerFinishTime.getCurrentMinute());

            values.put(Contract.StaffAvailability.START_TIME, startTime.getTimeInMillis());
            values.put(Contract.StaffAvailability.FINISH_TIME, finishTime.getTimeInMillis());

            getContentResolver()
                    .insert(Contract.StaffAvailability.CONTENT_URI, values).getLastPathSegment();


        }
    }


    private void startSelectDayDialog() {
        final String[] days = getResources().getStringArray(R.array.week_days);
        final boolean[] checkedItems = new boolean[days.length];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, days);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMultiChoiceItems(days, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(1);
                int day = (which + 2) % 7;
                day = day == 0 ? 7 : day;
                cal.set(Calendar.DAY_OF_WEEK, day);
                if (isChecked) {
                    mDateList.add(cal.getTime());
                } else {
                    mDateList.remove(cal.getTime());
                }
            }
        });
        // On positive button press, simply replace mDateList with selectedDates from v
        builder.setPositiveButton("Select Dates", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create().show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_availability, menu);
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
