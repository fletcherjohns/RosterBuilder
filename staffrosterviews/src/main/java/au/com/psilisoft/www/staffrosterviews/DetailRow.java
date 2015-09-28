package au.com.psilisoft.www.staffrosterviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Fletcher on 23/09/2015.
 *
 * A compound view that is composed of a TextView label and a Widget for selecting/creating a value.
 * It is intended to be used within a UI for data entry. It should be extended, and a specific
 * Widget returned with the getValueWidget() method. This widget must save its own state via
 * onSaveInstanceState() and onRestoreInstanceState(Parcelable state)
 */
public abstract class DetailRow<E> extends LinearLayout {

    private static final String SUPER_INSTANCE_STATE = "saved_instance_state_parcelable";
    private static final String STATE_VIEW_IDS = "state_view_ids";
    private static final String STATE_ORIGINAL_VALUE = "state_original_value";

    private TextView mLabel;
    private View mValueWidget;
    /**
     * This value is set when the value widget is first created and when init(E) is called
     */
    private E mOriginalValue;
    /**
     * These ids correspond to each of the child views. In this case there are only ever 2 children,
     * however in larger layouts this should still work.
     * mViewIds[i] == getChildAt(i).getId()
     */
    private int[] mViewIds;

    private DetailRowCallback mCallback;
    private Paint mPaint;

    public DetailRow(Context context) {
        this(context, null);
    }

    public DetailRow(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.DetailRowStyle);
    }

    public DetailRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        boolean alignRight;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DetailRow, defStyleAttr, 0);
        try {
            alignRight = a.getInt(R.styleable.DetailRow_Align, 1) == 1;
        } finally {
            a.recycle();
        }

        LinearLayout.LayoutParams labelParams;
        labelParams = new LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (alignRight) {
            labelParams.weight = 1;
        } else {
            labelParams.width = LayoutParams.WRAP_CONTENT;
        }

        LinearLayout.LayoutParams valueParams;
        valueParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // Create and add both children to the layout
        mLabel = new TextView(context, attrs, defStyleAttr);
        mLabel.setLayoutParams(labelParams);
        addView(mLabel);
        // The value widget is created by subclass
        mValueWidget = getValueWidget(context, attrs, defStyleAttr);
        mValueWidget.setLayoutParams(valueParams);

        addView(mValueWidget);
        // The value itself must also be retrieved by subclass
        mOriginalValue = getValue();

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);

    }



    /**
     * Set the value of the value widget and overwrite the original value
     * @param value
     */
    public void init(E value) {
        mOriginalValue = value;
        setValue(value);
    }

    /**
     * Has the value widget been changed/edited
     * @return whether the value shown has changed from the original value
     */
    public boolean isEdited() {
        return !mOriginalValue.equals(getValue());
    }

    /**
     * Set the callback for this DetailRow. It will be triggered whenever a change has been made
     * @param callback
     */
    public void setCallback(DetailRowCallback callback) {
        mCallback = callback;
    }

    /**
     * This method should be called by subclasses whenever the value widget has changed
     */
    protected void onValueChanged() {
        if (mCallback != null) mCallback.onValueChanged(isEdited());
    }

    /**
     * Construct a widget View using the View(Context, AttributeSet) constructor, and return it.
     * Any attributes for this widget can be placed in the parent DetailRow tag. These attributes
     * will be passed to the TextView label as well.
     * @param context
     * @param attrs
     * @return value widget View
     */
    protected abstract View getValueWidget(Context context, AttributeSet attrs, int defStyleAttrs);

    /**
     * Set the value of the value widget.
     * @param value
     */
    public abstract void setValue(E value);

    /**
     * Get the value of the value widget.
     * @return the current value of the value widget.
     */
    public abstract E getValue();

    /**
     * This is called from onSaveInstanceState(). As the type of value is unknown to DetailRow,
     * this method is required to put it in the bundle.
     * Simply put value in the bundle using the supplied tag.
     * The value widget will save its own state if it is able. Otherwise save its state here.
     * @param value the value to put in the bundle
     * @param bundle the bundle to put the value in
     * @param tag the tag to assign to the value
     */
    protected abstract void putValueInTheBundle(E value, Bundle bundle, String tag);

    /**
     * This is called from onRestoreInstanceState(Parcelable state). As with putValueInTheBundle,
     * simply get the value back out of the bundle using the supplied tag.
     * @param bundle the bundle to get the value out of
     * @param tag the tag of the value required
     * @return the value from the bundle
     */
    protected abstract E getValueBackOutOfTheBundle(Bundle bundle, String tag);

    /**
     * Save the state of this view and ensure all views have unique ids. Also save these unique ids.
     * This will allow each child to save its own state.
     * @return Parcelable containing state.
     */
    @Override
    protected Parcelable onSaveInstanceState() {

        // Create a bundle to put super parcelable in
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState());
        // Use abstract method to put mOriginalValue in the bundle;
        putValueInTheBundle(mOriginalValue, bundle, STATE_ORIGINAL_VALUE);
        // Store mViewIds in the bundle - initialize if necessary.
        if (mViewIds == null) {
            // We need as many ids as child views
            mViewIds = new int[getChildCount()];
            for (int i = 0; i < mViewIds.length; i++) {
                // generate a unique id for each view
                mViewIds[i] = View.generateViewId();
                // assign the id to the view at the same index
                getChildAt(i).setId(mViewIds[i]);
            }
        }
        bundle.putIntArray(STATE_VIEW_IDS, mViewIds);
        // return the bundle
        return bundle;
    }

    /**
     * Parcelable is instanceof Bundle. Get all state information out of the Bundle along with the
     * state Parcelable received from super.onSaveInstanceState().
     * Pass state to super.onRestoreInstanceState(Parcelable)
     * @param state the Bundle returned from onSaveInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        // We know state is a Bundle:
        Bundle bundle = (Bundle) state;
        // Get mViewIds out of the bundle
        mViewIds = bundle.getIntArray(STATE_VIEW_IDS);
        // For each id, assign to the view of same index
        if (mViewIds != null) {
            for (int i = 0; i < mViewIds.length; i++) {
                getChildAt(i).setId(mViewIds[i]);
            }
        }
        // Get mOriginalValue out of the bundle
        mOriginalValue = getValueBackOutOfTheBundle(bundle, STATE_ORIGINAL_VALUE);
        // get super parcelable back out of the bundle and pass it to
        // super.onRestoreInstanceState(Parcelable)
        state = bundle.getParcelable(SUPER_INSTANCE_STATE);
        super.onRestoreInstanceState(state);
    }

    /**
     * This callback simply notifies when the value of the value widget has changed.
     */
    public interface DetailRowCallback {
        /**
         * The value of the value widget has been changed.
         * @param isEdited boolean value whether the value widget contains a value other than what
         *                 it started with
         */
        void onValueChanged(boolean isEdited);
    }
}
