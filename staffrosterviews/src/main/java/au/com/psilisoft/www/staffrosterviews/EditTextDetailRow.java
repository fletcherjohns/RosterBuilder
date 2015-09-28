package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Fletcher on 28/09/2015.
 */
public class EditTextDetailRow extends DetailRow<String> {

    private EditText mEditText;

    public EditTextDetailRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText.setText("");
    }

    @Override
    protected View getValueWidget(Context context, AttributeSet attrs, int defStyleAttrs) {
        if (mEditText == null) {
            mEditText = new EditText(context, attrs);
            mEditText.setBackground(null);
            mEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    onValueChanged();
                }
            });
        }
        return mEditText;
    }

    @Override
    public void setValue(String value) {
        mEditText.setText(value);
    }

    @Override
    public String getValue() {
        return mEditText.getText().toString();
    }

    @Override
    protected void putValueInTheBundle(String value, Bundle bundle, String tag) {
        bundle.putString(tag, value);
    }

    @Override
    protected String getValueBackOutOfTheBundle(Bundle bundle, String tag) {
        return bundle.getString(tag);
    }
}
