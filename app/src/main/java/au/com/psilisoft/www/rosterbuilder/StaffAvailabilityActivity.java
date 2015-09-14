package au.com.psilisoft.www.rosterbuilder;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import au.com.psilisoft.www.rosterbuilder.custom_objects.Availability;
import au.com.psilisoft.www.rosterbuilder.provider.Contract;


public class StaffAvailabilityActivity extends SimpleListActivity {

    public static final String EXTRA_ID = "extra_id";

    @Override
    protected String getMessage() {
        return "Availability list";
    }

    protected List<Availability> getList() {

        String where = Contract.StaffAvailability.STAFF_ID + "=?";
        String[] whereArgs = {String.valueOf(getIntent().getLongExtra(EXTRA_ID, -1))};
        Cursor c = getContentResolver().query(Contract.StaffAvailability.CONTENT_URI,
                null, where, whereArgs, null);
        if (c.moveToFirst()) {
            List<Availability> list = new ArrayList<>();
            do {
                list.add(new Availability(c));
            } while (c.moveToNext());
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new AvailabilityAdapter(this, getList());
    }

    @Override
    protected void updateAdapter(BaseAdapter adapter) {
        ((AvailabilityAdapter)adapter).setList(getList());
    }

    @Override
    protected void onActionNewClicked() {
        startCreateAvailabilityActivity();
    }

    @Override
    protected void onListItemClick(Object object) {
        showAvailabilityDialog((Availability) object);
    }

    @Override
    protected void delete(long id) {
        getContentResolver().delete(Uri.withAppendedPath(Contract.StaffAvailability.CONTENT_URI,
                String.valueOf(id)), null, null);
    }

    private void startCreateAvailabilityActivity() {
        Intent intent = new Intent(this, CreateAvailabilityActivity.class);
        intent.putExtra(CreateAvailabilityActivity.EXTRA_ID, getIntent().getLongExtra(EXTRA_ID, -1));
        startActivity(intent);
    }

    private void showAvailabilityDialog(Availability availability) {

        final long id;
        View view = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_availability, null);
        final Spinner spinnerDay = (Spinner) view.findViewById(R.id.spinner_day);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.week_days));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);
        final TimePicker pickerStartTime = (TimePicker) view.findViewById(R.id.picker_start_time);
        final TimePicker pickerFinishTime = (TimePicker) view.findViewById(R.id.picker_finish_time);
        if (availability != null) {
            spinnerDay.setSelection((availability.getDay() + 5) % 7);
            pickerStartTime.setCurrentHour(availability.getStartTime().get(Calendar.HOUR_OF_DAY));
            pickerStartTime.setCurrentMinute(availability.getStartTime().get(Calendar.MINUTE));
            pickerFinishTime.setCurrentHour(availability.getFinishTime().get(Calendar.HOUR_OF_DAY));
            pickerFinishTime.setCurrentMinute(availability.getFinishTime().get(Calendar.MINUTE));
            id = availability.getId();
        } else {
            id = -1;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(id > 0 ? "Update Availability" : "Create New")
                .setMessage("Set day, start and finish times")
                .setView(view)
                .setPositiveButton(id > 0 ? "UPDATE" : "CREATE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Calendar startTime = new GregorianCalendar();
                                int day = spinnerDay.getSelectedItemPosition() + 2;
                                day = day == 0 ? 7 : day;
                                startTime.set(Calendar.DAY_OF_WEEK, day);
                                startTime.set(Calendar.HOUR_OF_DAY, pickerStartTime.getCurrentHour());
                                startTime.set(Calendar.MINUTE, pickerStartTime.getCurrentMinute());
                                Calendar finishTime = new GregorianCalendar();
                                finishTime.set(Calendar.DAY_OF_WEEK, day);
                                finishTime.set(Calendar.HOUR_OF_DAY, pickerFinishTime.getCurrentHour());
                                finishTime.set(Calendar.MINUTE, pickerFinishTime.getCurrentMinute());

                                ContentValues values = new ContentValues();
                                values.put(Contract.StaffAvailability.STAFF_ID,
                                        getIntent().getLongExtra(EXTRA_ID, -1));
                                values.put(Contract.StaffAvailability.START_TIME,
                                        startTime.getTimeInMillis());
                                values.put(Contract.StaffAvailability.FINISH_TIME,
                                        finishTime.getTimeInMillis());
                                if (id > 0) {
                                    updateAvailability(id, values);
                                } else {
                                    insertAvailability(values);
                                }
                            }
                        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create().show();
    }

    private void updateAvailability(long id, ContentValues values) {
        getContentResolver().update(Uri.withAppendedPath(Contract.StaffAvailability.CONTENT_URI,
                String.valueOf(id)), values, null, null);
        updateAdapter();
    }

    private void insertAvailability(ContentValues values) {
        getContentResolver().insert(Contract.StaffAvailability.CONTENT_URI, values);
        updateAdapter();
    }

}
