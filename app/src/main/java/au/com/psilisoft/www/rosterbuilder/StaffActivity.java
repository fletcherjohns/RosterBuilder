package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import au.com.psilisoft.www.rosterbuilder.provider.Contract;


public class StaffActivity extends Activity {

    public static final String EXTRA_ID = "extra_id";

    private EditText mEditName;
    private EditText mEditMinShifts;
    private EditText mEditMaxShifts;
    private EditText mEditMinHours;
    private EditText mEditMaxHours;
    private Button mButtonAbilities;
    private Button mButtonAvailabilities;
    private Button mButtonUnavailabilities;

    private Watcher mWatcher = new Watcher();
    private Listener mListener = new Listener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        mEditName = (EditText) findViewById(R.id.edit_text_name);
        mEditMinShifts = (EditText) findViewById(R.id.edit_text_minimum_shifts);
        mEditMaxShifts = (EditText) findViewById(R.id.edit_text_maximum_shifts);
        mEditMinHours = (EditText) findViewById(R.id.edit_text_minimum_hours);
        mEditMaxHours = (EditText) findViewById(R.id.edit_text_maximum_hours);
        mButtonAbilities = (Button) findViewById(R.id.button_staff_abilities);
        mButtonAbilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStaffAbilitiesActivity();
            }
        });
        mButtonAvailabilities = (Button) findViewById(R.id.button_staff_availabilities);
        mButtonAvailabilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStaffAvailabilitiesActivity();
            }
        });
        mButtonUnavailabilities = (Button) findViewById(R.id.button_staff_unavailabilities);
        mButtonUnavailabilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStaffUnavailabilitiesActivity();
            }
        });

        Cursor c = getContentResolver().query(Uri.withAppendedPath(Contract.Staff.CONTENT_URI,
                String.valueOf(getIntent().getLongExtra(EXTRA_ID, -1))), null, null, null, null);
        if (c.moveToFirst()) {
            mEditName.setText(c.getString(c.getColumnIndex(Contract.Staff.NAME)));
            mEditMinShifts.setText(String.valueOf(c.getLong(c.getColumnIndex(Contract.Staff.MIN_SHIFTS))));
            mEditMaxShifts.setText(String.valueOf(c.getLong(c.getColumnIndex(Contract.Staff.MAX_SHIFTS))));
            mEditMinHours.setText(String.valueOf(c.getLong(c.getColumnIndex(Contract.Staff.MIN_HOURS))));
            mEditMaxHours.setText(String.valueOf(c.getLong(c.getColumnIndex(Contract.Staff.MAX_HOURS))));
        } else {
            finish();
        }
        c.close();

        mEditName.addTextChangedListener(mWatcher);
        mEditMinShifts.addTextChangedListener(mWatcher);
        mEditMaxShifts.addTextChangedListener(mWatcher);
        mEditMinHours.addTextChangedListener(mWatcher);
        mEditMaxHours.addTextChangedListener(mWatcher);

        mEditName.setOnFocusChangeListener(mListener);
        mEditMinShifts.setOnFocusChangeListener(mListener);
        mEditMaxShifts.setOnFocusChangeListener(mListener);
        mEditMinHours.setOnFocusChangeListener(mListener);
        mEditMaxHours.setOnFocusChangeListener(mListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_staff, menu);
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

    private void startStaffAbilitiesActivity() {
        Intent intent = new Intent(this, ObjectAbilitiesActivity.class);
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_ID, getIntent().getLongExtra(EXTRA_ID, -1));
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_LINK_CONTENT_URI,
                Contract.StaffAbilities.CONTENT_URI);
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_ABILITY_CONTENT_URI,
                Contract.Ability.CONTENT_URI);
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_COLUMN_LINK_TABLE_OBJECT_ID,
                Contract.StaffAbilities.STAFF_ID);
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_COLUMN_LINK_TABLE_ABILITY_ID,
                Contract.StaffAbilities.ABILITY_ID);
        intent.putExtra(ObjectAbilitiesActivity.EXTRA_COLUMN_ABILITY_TABLE_NAME,
                Contract.Ability.NAME);
        startActivity(intent);
    }

    private void startStaffAvailabilitiesActivity() {
        Intent intent = new Intent(this, StaffAvailabilityActivity.class);
        intent.putExtra(StaffAvailabilityActivity.EXTRA_ID, getIntent().getLongExtra(EXTRA_ID, -1));
        startActivity(intent);
    }

    private void startStaffUnavailabilitiesActivity() {
        Intent intent = new Intent(this, StaffUnavailabilityActivity.class);
        intent.putExtra(StaffUnavailabilityActivity.EXTRA_ID, getIntent().getLongExtra(EXTRA_ID, -1));
        startActivity(intent);
    }

    private class Listener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {

                String s = ((EditText)v).getText().toString();
                if (v == mEditName) {
                    if (mEditName.getText().toString().isEmpty()) {
                        mEditName.setText("New Staff");
                    }
                } else {
                    try {
                        Integer.valueOf(s);
                    } catch (NumberFormatException e) {
                        ((EditText)v).setText("0");
                    }
                }
            }
        }
    }
    private class Watcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            ContentValues values = new ContentValues();
            if (s == mEditName.getText()) {
                if (!s.toString().isEmpty()) {
                    values.put(Contract.Staff.NAME, s.toString());
                } else {
                    values.put(Contract.Staff.NAME, "New Staff");
                }
            } else {
                int n;
                try {
                    n = Integer.valueOf(s.toString());
                } catch (NumberFormatException e) {
                    n = 0;
                }
                if (s == mEditMinShifts.getText()) {
                    values.put(Contract.Staff.MIN_SHIFTS, n);
                } else if (s == mEditMaxShifts.getText()){
                    values.put(Contract.Staff.MAX_SHIFTS, n);
                } else if (s == mEditMinHours.getText()) {
                    values.put(Contract.Staff.MIN_HOURS, n);
                } else if (s == mEditMaxHours.getText()) {
                    values.put(Contract.Staff.MAX_HOURS, n);
                }
            }

            getContentResolver().update(Uri.withAppendedPath(Contract.Staff.CONTENT_URI,
                    String.valueOf(getIntent().getLongExtra(EXTRA_ID, -1))), values, null, null);
        }
    }
}
