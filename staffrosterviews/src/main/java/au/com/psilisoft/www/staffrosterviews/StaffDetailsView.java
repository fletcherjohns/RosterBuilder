package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import au.com.psilisoft.www.staffrosterviews.detailrows.ComboNumberRollerPickerDetailRow;
import au.com.psilisoft.www.staffrosterviews.detailrows.DetailRow;
import au.com.psilisoft.www.staffrosterviews.detailrows.EditTextDetailRow;
import au.com.psilisoft.www.staffrosterviews.detailrows.NumberRollerPickerDetailRow;

/**
 * Created by Fletcher on 14/09/2015.
 */
public class StaffDetailsView extends LinearLayout {

    private long mStaffId;
    private EditTextDetailRow mNameDetailRow;
    private NumberRollerPickerDetailRow mMinShiftsDetailRow;
    private NumberRollerPickerDetailRow mMaxShiftsDetailRow;
    private NumberRollerPickerDetailRow mMinHoursDetailRow;
    private NumberRollerPickerDetailRow mMaxHoursDetailRow;
    private NumberRollerPickerDetailRow mMinConsecDaysOffDetailRow;

    public StaffDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(context, R.layout.view_staff_details, this);
        setOrientation(VERTICAL);
        /*setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));*/
        mNameDetailRow = (EditTextDetailRow) findViewById(R.id.edit_staff_name);
        mMinShiftsDetailRow = ((ComboNumberRollerPickerDetailRow)
                findViewById(R.id.combo_staff_shifts)).getDetailRows().get(0);
        mMaxShiftsDetailRow = ((ComboNumberRollerPickerDetailRow)
                findViewById(R.id.combo_staff_shifts)).getDetailRows().get(1);
        mMinHoursDetailRow = ((ComboNumberRollerPickerDetailRow)
                findViewById(R.id.combo_staff_hours)).getDetailRows().get(0);
        mMaxHoursDetailRow = ((ComboNumberRollerPickerDetailRow)
                findViewById(R.id.combo_staff_hours)).getDetailRows().get(1);
        mMinConsecDaysOffDetailRow = (NumberRollerPickerDetailRow)
                findViewById(R.id.picker_staff_min_consec_days_off);
    }

    public StaffDetailsView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.StaffDetailsStyle);
    }

    public void init(long staffId, String name, int minShifts, int maxShifts,
                     int minHours, int maxHours, int minConsecDaysOff) {
        mStaffId = staffId;
        mNameDetailRow.init(name);
        mMinShiftsDetailRow.init(minShifts);
        mMaxShiftsDetailRow.init(maxShifts);
        mMinHoursDetailRow.init(minHours);
        mMaxHoursDetailRow.init(maxHours);
        mMinConsecDaysOffDetailRow.init(minConsecDaysOff);
    }

    public void setStaffId(long staffId) {
        mStaffId = staffId;
    }
    public long getStaffId() {
        return mStaffId;
    }

    public void setStaffName(String name) {
        mNameDetailRow.setValue(name);
    }
    public String getStaffName() {
        return mNameDetailRow.getValue();
    }

    public void setMinShifts(int minShifts) {
        mMinShiftsDetailRow.setValue(minShifts);
    }
    public int getMinShifts() {
        return mMinShiftsDetailRow.getValue();
    }

    public void setMaxShifts(int maxShifts) {
        mMaxShiftsDetailRow.setValue(maxShifts);
    }
    public int getMaxShifts() {
        return mMaxShiftsDetailRow.getValue();
    }

    public void setMinHours(int minHours) {
        mMinHoursDetailRow.setValue(minHours);
    }
    public int getMinHours() {
        return mMinHoursDetailRow.getValue();
    }

    public void setMaxHours(int maxHours) {
        mMaxHoursDetailRow.setValue(maxHours);
    }
    public int getMaxHours() {
        return mMaxHoursDetailRow.getValue();
    }

    public void setMinConsecDaysOff(int minConsecDaysOff) {
        mMinConsecDaysOffDetailRow.setValue(minConsecDaysOff);
    }
    public int getMinConsecDaysOff() {
        return mMinConsecDaysOffDetailRow.getValue();
    }

    public boolean isEdited() {
        return mNameDetailRow.isEdited()
                || mMinShiftsDetailRow.isEdited()
                || mMaxShiftsDetailRow.isEdited()
                || mMinHoursDetailRow.isEdited()
                || mMaxHoursDetailRow.isEdited()
                || mMinConsecDaysOffDetailRow.isEdited();
    }
}
