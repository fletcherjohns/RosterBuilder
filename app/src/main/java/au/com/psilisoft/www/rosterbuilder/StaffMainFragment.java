package au.com.psilisoft.www.rosterbuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.List;

import au.com.psilisoft.www.rosterbuilder.custom_objects.Ability;
import au.com.psilisoft.www.rosterbuilder.provider.Contract;
import au.com.psilisoft.www.rosterbuilder.provider.DatabaseManager;

public class StaffMainFragment extends Fragment {

    private EditText mEditTextName;
    private TextView mTextViewMinShifts;
    private TextView mTextViewMaxShifts;
    private TextView mTextViewMinHours;
    private TextView mTextViewMaxHours;
    private Button mButtonEditAbilities;
    private ListView mListViewAbilities;
    private Button mButtonAvailabilities;
    private Button mButtonUnavailabilities;

    private List<Ability> mAllAbilities;
    private ArrayAdapter<Ability> mAbilityAdapter;

    private StaffMainCallback mListener;
    private au.com.psilisoft.www.rosterbuilder.custom_objects.Staff mStaff;

    public static StaffMainFragment newInstance() {
        StaffMainFragment fragment = new StaffMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StaffMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_staff_main, container, false);
        findAllViewsById(v);
        return v;
    }

    private void findAllViewsById(View v) {

        mEditTextName = (EditText) v.findViewById(R.id.edit_text_name);
        mEditTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                ContentValues values = new ContentValues();
                if (s == mEditTextName.getText()) {
                    if (!s.toString().isEmpty()) {
                        values.put(Contract.Staff.NAME, s.toString());
                    } else {
                        values.put(Contract.Staff.NAME, "New Staff");
                    }

                }
                mListener.updateStaff(values);
            }
        });
        mEditTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (mEditTextName.getText().toString().isEmpty()) {
                    mEditTextName.setText("New Staff");
                }
            }
        });

        mTextViewMinShifts = (TextView) v.findViewById(R.id.text_view_minimum_shifts);
        mTextViewMinShifts.setOnClickListener(new NumberClickListener());

        mTextViewMaxShifts = (TextView) v.findViewById(R.id.text_view_maximum_shifts);
        mTextViewMaxShifts.setOnClickListener(new NumberClickListener());

        mTextViewMinHours = (TextView) v.findViewById(R.id.text_view_minimum_hours);
        mTextViewMinHours.setOnClickListener(new NumberClickListener());

        mTextViewMaxHours = (TextView) v.findViewById(R.id.text_view_maximum_hours);
        mTextViewMaxHours.setOnClickListener(new NumberClickListener());

        mButtonEditAbilities = (Button) v.findViewById(R.id.button_edit_abilities);
        mListViewAbilities = (ListView) v.findViewById(R.id.list_view_abilities);
        mButtonAvailabilities = (Button) v.findViewById(R.id.button_staff_availabilities);
        mButtonUnavailabilities = (Button) v.findViewById(R.id.button_staff_unavailabilities);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (StaffMainCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mStaff = mListener.getStaff();

        mAllAbilities = DatabaseManager.getAbilities(activity);
        mAbilityAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, mAllAbilities);
    }

    @Override
    public void onResume() {
        super.onResume();
        mListViewAbilities.setAdapter(mAbilityAdapter);
        for (Ability ability : mAllAbilities) {
            mListViewAbilities.setItemChecked(mAbilityAdapter.getPosition(ability),
                    mStaff.getAbilities().contains(ability));

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void startNumberPickerDialog(String title, int maxValue, int minValue, final String valueKey) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        NumberPicker v = (NumberPicker) ((LayoutInflater) builder.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_number_picker, null);
        v.setMaxValue(maxValue);
        v.setMinValue(minValue);
        v.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                ContentValues values = new ContentValues();
                values.put(valueKey, newVal);
                mListener.updateStaff(values);
            }
        });
        builder.setView(v).setTitle(title).create().show();
    }

    public void removeBottomButtons() {
        mButtonAvailabilities.setVisibility(View.GONE);
        mButtonUnavailabilities.setVisibility(View.GONE);
    }

    public interface StaffMainCallback {

        au.com.psilisoft.www.rosterbuilder.custom_objects.Staff getStaff();

        void updateStaff(ContentValues values);
    }

    private class NumberClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            String title;
            int maxValue;
            int minValue;
            String valueKey;

            if (v == mTextViewMinShifts) {
                title = "Minimum Required Shifts";
                maxValue = 7;
                minValue = 0;
                valueKey = Contract.Staff.MIN_SHIFTS;

            } else if (v == mTextViewMaxShifts) {
                title = "Maximum Required Shifts";
                maxValue = 7;
                minValue = 0;
                valueKey = Contract.Staff.MAX_SHIFTS;

            } else if (v == mTextViewMinHours) {
                title = "Minimum Required Hours";
                maxValue = 40;
                minValue = 0;
                valueKey = Contract.Staff.MIN_HOURS;

            } else if (v == mTextViewMaxHours) {
                title = "Maximum Required Hours";
                maxValue = 40;
                minValue = 0;
                valueKey = Contract.Staff.MAX_HOURS;
            } else {
                return;
            }
            startNumberPickerDialog(title, maxValue, minValue, valueKey);
        }
    }
}
