package au.com.psilisoft.www.rosterbuilder;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import au.com.psilisoft.www.rosterbuilder.custom_objects.Shift;
import au.com.psilisoft.www.rosterbuilder.custom_objects.Staff;
import au.com.psilisoft.www.rosterbuilder.custom_objects.TimeSegmentComparator;
import au.com.psilisoft.www.rosterbuilder.custom_objects.TimedSegment;

/**
 * Created by Fletcher on 24/07/2015.
 */
class RosterSegmentAdapter extends BaseAdapter {

    private Context mContext;
    private List<TimedSegment> mList;
    private int mEarliestTime;
    private int mLatestTime;

    public RosterSegmentAdapter(Context context, List<TimedSegment> list) {
        mContext = context;
        setList(list);
    }

    public void setList(List<TimedSegment> list) {
        mList = list;
        int size = mList.size();
        TimedSegment lhs;
        TimedSegment rhs;
        for (int i = 0; i < size; i++) {
            lhs = mList.get(i);
            for (int j = i + 1; j < size ; j++) {
                rhs = mList.get(j);
                if ((lhs.getDay() == rhs.getDay() && lhs.isShift() && !rhs.isShift())
                        || lhs.getStartTime().compareTo(rhs.getStartTime()) > 0) {
                    mList.set(i, rhs);
                    mList.set(j, lhs);
                    lhs = rhs;
                }
            }
        }
        mEarliestTime = 24 * 60;
        mLatestTime = 0;
        for (TimedSegment segment : mList) {
            checkEarliestAndLatest(segment);
        }
        notifyDataSetChanged();
    }

    private void checkEarliestAndLatest(TimedSegment segment) {

        int startTime = segment.getStartTime().get(Calendar.HOUR_OF_DAY) * 60
                + segment.getStartTime().get(Calendar.MINUTE);
        int finishTime = segment.getFinishTime().get(Calendar.HOUR_OF_DAY) * 60
                + segment.getFinishTime().get(Calendar.MINUTE);
        if (startTime < mEarliestTime) mEarliestTime = startTime;
        if (finishTime > mLatestTime) mLatestTime = finishTime;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        TimedSegment segment = mList.get(position);

        if (view == null) {
            holder = new ViewHolder();
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.row_roster_segment, parent, false);
            holder.layout = (LinearLayout) view.findViewById(R.id.layout);
            holder.textDay = (TextView) view.findViewById(R.id.text_view_day);
            holder.textStartTime = (TextView) view.findViewById(R.id.text_view_start_time);
            holder.textFinishTime = (TextView) view.findViewById(R.id.text_view_finish_time);
            holder.textAbilities = (TextView) view.findViewById(R.id.text_view_abilities);
            holder.textStaff = (TextView) view.findViewById(R.id.text_view_staff);
            holder.textDuration = (TextView) view.findViewById(R.id.text_view_duration);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (position == 0 || segment.getDay() != mList.get(position - 1).getDay()) {
            holder.textDay.setVisibility(View.VISIBLE);
            holder.textDay.setText(segment.getDateString());
        } else {
            holder.textDay.setVisibility(View.GONE);
        }
        holder.textStartTime.setText(segment.getStartTimeString());
        holder.textFinishTime.setText(segment.getFinishTimeString());
        holder.textAbilities.setText(segment.getAbilities().toString());
        Log.v("aaaa", segment.isShift() + ", " + segment.getAbilities().toString());
        Staff staff;
        if (segment instanceof Shift
                && (staff = ((Shift) segment).getStaff()) != null) {
            holder.textStaff.setText(staff.getName());
        } else {
            holder.textStaff.setText("");
        }
        holder.textDuration.setText(segment.getDurationString());

        if (segment.isShift()) {
            holder.layout.setBackgroundColor(Color.rgb(100, 150, 190));
        } else {
            holder.layout.setBackgroundColor(Color.rgb(160, 100, 120));
        }

        int startTime = segment.getStartTime().get(Calendar.HOUR_OF_DAY) * 60
                + segment.getStartTime().get(Calendar.MINUTE);
        int finishTime = segment.getFinishTime().get(Calendar.HOUR_OF_DAY) * 60
                + segment.getFinishTime().get(Calendar.MINUTE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.layout.getLayoutParams();
        params.width = (int) (parent.getWidth() * (finishTime - startTime) / (float) (mLatestTime - mEarliestTime));
        holder.layout.setTranslationX(parent.getWidth() * (startTime - mEarliestTime) / (float) (mLatestTime - mEarliestTime));
        return view;

    }

    private class ViewHolder {

        LinearLayout layout;
        TextView textDay;
        TextView textStartTime;
        TextView textFinishTime;
        TextView textAbilities;
        TextView textStaff;
        TextView textDuration;
    }
}
