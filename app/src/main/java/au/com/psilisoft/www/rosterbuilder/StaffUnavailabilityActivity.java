package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;


public class StaffUnavailabilityActivity extends Activity {

    public static final String EXTRA_ID = "extra_id";

    private long mStaffId;
    private SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM/yyyy");
    private CalendarPickerView mCalendarPickerView;
    private ContentValues mContentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_unavailability);

        mStaffId = getIntent().getLongExtra(EXTRA_ID, -1);

        mContentValues = new ContentValues();
        mContentValues.put(Contract.StaffUnavailability.STAFF_ID, mStaffId);

        Calendar minDate = new GregorianCalendar();
        minDate.add(Calendar.YEAR, -1);

        Calendar maxDate = new GregorianCalendar();
        maxDate.add(Calendar.YEAR, 1);

        mCalendarPickerView = (CalendarPickerView) findViewById(R.id.calendar_picker);
        mCalendarPickerView.init(minDate.getTime(), maxDate.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                .withSelectedDates(getList());
        mCalendarPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                insert(date);
            }

            @Override
            public void onDateUnselected(Date date) {
                delete(date);
            }
        });
    }

    private List<Date> getList() {

        String[] projection = {Contract.StaffUnavailability.DATE};
        String where = Contract.StaffUnavailability.STAFF_ID + "=?";
        String[] whereArgs = {String.valueOf(getIntent().getLongExtra(EXTRA_ID, -1))};
        Cursor c = getContentResolver().query(Contract.StaffUnavailability.CONTENT_URI,
                projection, where, whereArgs, null);

        List<Date> list = new ArrayList<>(c.getCount());

        if (c.moveToFirst()) {
            do {
                list.add(new Date(c.getLong(c
                        .getColumnIndex(Contract.StaffUnavailability.DATE))));
            } while (c.moveToNext());
        }
        return list;
    }

    private void delete(Date date) {
        String where = Contract.StaffUnavailability.STAFF_ID + "=? AND "
                + Contract.StaffUnavailability.DATE + "=?";
        String[] whereArgs = {String.valueOf(mStaffId), String.valueOf(date.getTime())};

        getContentResolver().delete(Contract.StaffUnavailability.CONTENT_URI, where, whereArgs);
    }

    private void insert(Date date) {
        mContentValues.put(Contract.StaffUnavailability.DATE, date.getTime());

        getContentResolver().insert(Contract.StaffUnavailability.CONTENT_URI, mContentValues);
    }

}
