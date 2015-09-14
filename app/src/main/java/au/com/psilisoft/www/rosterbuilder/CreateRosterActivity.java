package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;


public class CreateRosterActivity extends Activity {


    private EditText mEditName;
    private CalendarPickerView mPickerStartDate;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roster);


        mEditName = (EditText) findViewById(R.id.edit_text_name);
        mEditName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus && mEditName.getText().toString().isEmpty()) {
                    mEditName.setText("New Roster");
                }
            }
        });

        Calendar lastYear = new GregorianCalendar();
        lastYear.add(Calendar.YEAR, -1);

        Calendar nextYear = new GregorianCalendar();
        nextYear.add(Calendar.YEAR, 1);

        mPickerStartDate = (CalendarPickerView) findViewById(R.id.picker_start_date);
        mPickerStartDate.init(lastYear.getTime(), nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date());
        mPickerStartDate.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                List<Date> selectedDates = mPickerStartDate.getSelectedDates();
                if (selectedDates.size() > 1) {
                    Calendar first = new GregorianCalendar();
                    first.setTime(selectedDates.get(0));
                    Calendar second = new GregorianCalendar();
                    second.setTime(date);

                    while (first.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                        first.add(Calendar.DAY_OF_WEEK, -1);
                    }
                    while (second.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        second.add(Calendar.DAY_OF_WEEK, 1);
                    }
                    mPickerStartDate.selectDate(first.getTime(), true);
                    mPickerStartDate.selectDate(second.getTime(), true);
                }
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

        Button buttonCreate = (Button) findViewById(R.id.button_create);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Date> dates = mPickerStartDate.getSelectedDates();
                for (Date date : dates) {
                    Log.v("", date.toString());
                }
                if (dates.isEmpty()) return;

                ContentValues values = new ContentValues();
                values.put(Contract.Roster.NAME, mEditName.getText().toString());
                values.put(Contract.Roster.START_DATE, dates.get(0).getTime());
                values.put(Contract.Roster.END_DATE, dates.get(dates.size() - 1).getTime());

                long rosterId = Long.parseLong(getContentResolver()
                        .insert(Contract.Roster.CONTENT_URI, values).getLastPathSegment());

                startShiftListActivity(rosterId);
                finish();
            }
        });
    }

    private void startShiftListActivity(long rosterId) {
        Intent intent = new Intent(this, ShiftListActivity.class);
        intent.putExtra(ShiftListActivity.EXTRA_ID, rosterId);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_roster, menu);
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
